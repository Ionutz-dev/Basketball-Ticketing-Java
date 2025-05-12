package app.network.rpcprotocol;

import app.model.Match;
import app.model.User;
import app.network.dto.DTOUtils;
import app.network.dto.MatchDTO;
import app.network.dto.TicketDTO;
import app.network.dto.UserDTO;
import app.services.BasketballException;
import app.services.IBasketballObserver;
import app.services.IBasketballServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.StreamSupport;

public class BasketballServicesRpcProxy implements IBasketballServices {
    private static final Logger logger = LogManager.getLogger(BasketballServicesRpcProxy.class);

    private final String host;
    private final int port;

    private IBasketballObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private final BlockingQueue<Response> responses;
    private volatile boolean finished;

    public BasketballServicesRpcProxy(String host, int port) {
        logger.info("Creating RPC Proxy for server at {}:{}", host, port);
        this.host = host;
        this.port = port;
        responses = new LinkedBlockingQueue<>();
    }

    private void initializeConnection() throws BasketballException {
        logger.debug("Initializing connection to server at {}:{}", host, port);
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
            logger.info("Connection established to server at {}:{}", host, port);
        } catch (IOException e) {
            logger.error("Failed to connect to server at {}:{}", host, port, e);
            throw new BasketballException("Error connecting to server", e);
        }
    }

    private void startReader() {
        logger.debug("Starting reader thread for server responses");
        Thread readerThread = new Thread(new ReaderThread());
        readerThread.start();
        logger.debug("Reader thread started");
    }

    private void sendRequest(Request request) throws BasketballException {
        logger.debug("Sending request: {}", request.getType());
        try {
            output.writeObject(request);
            output.flush();
            logger.trace("Request sent");
        } catch (IOException e) {
            logger.error("Error sending request: {}", request.getType(), e);
            throw new BasketballException("Error sending request: " + e.getMessage(), e);
        }
    }

    private Response readResponse() throws BasketballException {
        logger.debug("Waiting for server response");
        try {
            Response response = responses.take();
            logger.debug("Received response: {}", response.getType());
            return response;
        } catch (InterruptedException e) {
            logger.error("Interrupted while waiting for response", e);
            throw new BasketballException("Error reading response: " + e.getMessage(), e);
        }
    }

    @Override
    public User login(String username, String password, IBasketballObserver clientObserver) throws BasketballException {
        logger.info("Login attempt: username={}", username);

        if (output == null || input == null || connection == null) {
            logger.debug("Connection not initialized, creating new connection");
            initializeConnection();
        }

        this.client = clientObserver;

        String[] credentials = new String[]{username, password};
        Request request = new Request(RequestType.LOGIN, credentials);
        sendRequest(request);
        Response response = readResponse();
        logger.debug("Login response received: {}", response.getType());

        if (response.getType() == ResponseType.LOGIN_SUCCESS) {
            UserDTO userDTO = (UserDTO) response.getData();
            User user = DTOUtils.getFromDTO(userDTO);
            logger.info("Login successful: username={}, id={}", username, user.getId());
            return user;
        } else if (response.getType() == ResponseType.LOGIN_FAILURE) {
            logger.warn("Login failed: username={}", username);
            return null;
        } else {
            logger.error("Unexpected response during login: {}", response.getType());
            throw new BasketballException("Unexpected response during login: " + response.getType());
        }
    }

    @Override
    public void logout(User user) throws BasketballException {
        logger.info("Logout: username={}, id={}", user.getUsername(), user.getId());

        UserDTO userDTO = DTOUtils.getDTO(user);
        Request request = new Request(RequestType.LOGOUT, userDTO);
        sendRequest(request);

        Response response = readResponse();
        logger.debug("Logout response received: {}", response.getType());

        if (response.getType() != ResponseType.OK) {
            logger.error("Logout failed: {}", response.getData());
            throw new BasketballException("Logout failed: " + response.getData());
        }

        closeConnection();
        logger.info("Logout successful and connection closed");
    }

    @Override
    public Iterable<Match> getAvailableMatches() throws BasketballException {
        logger.info("Getting available matches");

        Request request = new Request(RequestType.GET_AVAILABLE_MATCHES, null);
        sendRequest(request);

        Response response = readResponse();
        logger.debug("Available matches response received: {}", response.getType());

        if (response.getType() == ResponseType.MATCHES) {
            MatchDTO[] dtos = (MatchDTO[]) response.getData();

            Iterable<Match> matches = StreamSupport.stream(java.util.Arrays.spliterator(dtos), false)
                    .map(DTOUtils::getFromDTO)
                    .toList();

            logger.info("Received {} available matches", ((Iterable<?>) matches).spliterator().estimateSize());
            return matches;
        } else {
            logger.error("Failed to fetch matches: {}", response.getType());
            throw new BasketballException("Failed to fetch matches: " + response.getType());
        }
    }

    @Override
    public void sellTicket(int matchId, int userId, String customerName, int seatsToBuy) throws BasketballException {
        logger.info("Selling ticket: matchId={}, userId={}, customer={}, seats={}",
                matchId, userId, customerName, seatsToBuy);

        TicketDTO ticketDTO = new TicketDTO(0, matchId, userId, customerName, seatsToBuy);
        Request request = new Request(RequestType.SELL_TICKET, ticketDTO);
        sendRequest(request);

        Response response = readResponse();
        logger.debug("Sell ticket response received: {}", response.getType());

        if (response.getType() != ResponseType.TICKET_CONFIRMED) {
            logger.error("Ticket selling failed: {}", response.getData());
            throw new BasketballException("Ticket selling failed: " + response.getData());
        }

        logger.info("Ticket sold successfully");
    }

    private void closeConnection() {
        logger.debug("Closing connection to server");

        finished = true;
        try {
            if (input != null) {
                logger.trace("Closing input stream");
                input.close();
            }

            if (output != null) {
                logger.trace("Closing output stream");
                output.close();
            }

            if (connection != null) {
                logger.trace("Closing socket connection");
                connection.close();
            }

            client = null;
            logger.debug("Connection closed");
        } catch (IOException e) {
            logger.error("Error closing connection", e);
        }
    }

    private boolean isObserverUpdate(Response response) {
        return response.getType() == ResponseType.TICKET_SOLD;
    }

    private class ReaderThread implements Runnable {
        private final Logger threadLogger = LogManager.getLogger(ReaderThread.class);

        @Override
        public void run() {
            threadLogger.info("Response reader thread started");

            while (!finished) {
                try {
                    threadLogger.debug("Waiting for server message");
                    Object obj = input.readObject();

                    if (obj == null) {
                        threadLogger.warn("Received null object from server");
                        continue;
                    }

                    threadLogger.trace("Received object of type: {}", obj.getClass().getName());

                    if (obj instanceof Response) {
                        Response resp = (Response) obj;
                        threadLogger.debug("Received response: {}", resp.getType());

                        if (isObserverUpdate(resp)) {
                            threadLogger.debug("Processing observer update");

                            if (client != null) {
                                try {
                                    client.ticketSoldUpdate();
                                    threadLogger.debug("Client notified of ticket update");
                                } catch (BasketballException e) {
                                    threadLogger.error("Failed to notify client on ticket update", e);
                                }
                            } else {
                                threadLogger.warn("Cannot process observer update - client is null");
                            }
                        } else {
                            threadLogger.debug("Adding response to queue: {}", resp.getType());
                            responses.put(resp);
                        }
                    } else {
                        threadLogger.warn("Received unknown object type: {}", obj.getClass().getName());
                    }
                } catch (IOException e) {
                    threadLogger.error("IO error in reader thread", e);
                    finished = true;
                } catch (ClassNotFoundException e) {
                    threadLogger.error("Class not found in reader thread", e);
                    finished = true;
                } catch (InterruptedException e) {
                    threadLogger.error("Reader thread interrupted", e);
                }
            }

            threadLogger.info("Response reader thread ended");
        }
    }
}