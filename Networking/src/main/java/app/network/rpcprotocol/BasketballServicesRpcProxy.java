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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.StreamSupport;

public class BasketballServicesRpcProxy implements IBasketballServices {
    private final String host;
    private final int port;

    private IBasketballObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private final BlockingQueue<Response> responses;
    private volatile boolean finished;

    public BasketballServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        responses = new LinkedBlockingQueue<>();
    }

    private void initializeConnection() throws BasketballException {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            throw new BasketballException("Error connecting to server", e);
        }
    }

    private void startReader() {
        Thread readerThread = new Thread(new ReaderThread());
        readerThread.start();
    }

    private void sendRequest(Request request) throws BasketballException {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new BasketballException("Error sending request: " + e.getMessage(), e);
        }
    }

    private Response readResponse() throws BasketballException {
        try {
            return responses.take();
        } catch (InterruptedException e) {
            throw new BasketballException("Error reading response: " + e.getMessage(), e);
        }
    }

    @Override
    public User login(String username, String password, IBasketballObserver clientObserver) throws BasketballException {
        if (output == null || input == null || connection == null) {
            initializeConnection();
        }
        this.client = clientObserver;

        String[] credentials = new String[]{username, password};
        Request request = new Request(RequestType.LOGIN, credentials);
        sendRequest(request);
        Response response = readResponse();

        if (response.getType() == ResponseType.LOGIN_SUCCESS) {
            return DTOUtils.getFromDTO((UserDTO) response.getData());
        } else if (response.getType() == ResponseType.LOGIN_FAILURE) {
            return null;
        } else {
            throw new BasketballException("Unexpected response during login: " + response.getType());
        }
    }

    @Override
    public void logout(User user) throws BasketballException {
        UserDTO userDTO = DTOUtils.getDTO(user);
        sendRequest(new Request(RequestType.LOGOUT, userDTO));
        Response response = readResponse();
        if (response.getType() != ResponseType.OK) {
            throw new BasketballException("Logout failed: " + response.getData());
        }
        closeConnection();
    }

    @Override
    public Iterable<Match> getAvailableMatches() throws BasketballException {
        sendRequest(new Request(RequestType.GET_AVAILABLE_MATCHES, null));
        Response response = readResponse();

        if (response.getType() == ResponseType.MATCHES) {
            MatchDTO[] dtos = (MatchDTO[]) response.getData();
            return StreamSupport.stream(java.util.Arrays.spliterator(dtos), false)
                    .map(DTOUtils::getFromDTO)
                    .toList();
        } else {
            throw new BasketballException("Failed to fetch matches: " + response.getType());
        }
    }

    @Override
    public void sellTicket(int matchId, int userId, String customerName, int seatsToBuy) throws BasketballException {
        TicketDTO ticketDTO = new TicketDTO(0, matchId, userId, customerName, seatsToBuy);
        Request request = new Request(RequestType.SELL_TICKET, ticketDTO);
        sendRequest(request);
        Response response = readResponse();

        if (response.getType() != ResponseType.TICKET_CONFIRMED) {
            throw new BasketballException("Ticket selling failed: " + response.getData());
        }
    }

    private void closeConnection() {
        finished = true;
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (connection != null) connection.close();
            client = null;
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    private boolean isObserverUpdate(Response response) {
        return response.getType() == ResponseType.TICKET_SOLD;
    }

    private class ReaderThread implements Runnable {
        @Override
        public void run() {
            while (!finished) {
                try {
                    Object obj = input.readObject();
                    if (obj instanceof Response resp) {
                        if (isObserverUpdate(resp)) {
                            if (client != null) {
                                try {
                                    client.ticketSoldUpdate();
                                } catch (BasketballException e) {
                                    System.err.println("[Proxy] Failed to notify client on ticket update: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            responses.put(resp);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    finished = true;
                    System.err.println("Reader thread stopped: " + e.getMessage());
                } catch (InterruptedException e) {
                    System.err.println("Reader thread interrupted: " + e.getMessage());
                }
            }
        }
    }
}
