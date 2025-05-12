package app.network.rpcprotocol;

import app.model.Match;
import app.model.Ticket;
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
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.stream.StreamSupport;

public class BasketballClientRpcReflectionWorker implements Runnable, IBasketballObserver {
    private static final Logger logger = LogManager.getLogger(BasketballClientRpcReflectionWorker.class);

    private final IBasketballServices server;
    private final Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    private User user;

    public BasketballClientRpcReflectionWorker(IBasketballServices server, Socket connection) {
        logger.info("Creating worker for client: {}", connection.getRemoteSocketAddress());
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
            logger.debug("Initialized IO streams");
        } catch (IOException e) {
            logger.error("Error initializing worker", e);
        }
    }

    @Override
    public void run() {
        logger.info("Worker thread started");
        try {
            while (connected) {
                logger.debug("Waiting for client request");
                Object request = input.readObject();

                if (request == null) {
                    logger.warn("Received null request, closing connection");
                    break;
                }

                logger.debug("Received request: {}", request);

                if (request instanceof Request) {
                    Response response = handleRequest((Request) request);
                    if (response != null) {
                        logger.debug("Sending response: {}", response);
                        sendResponse(response);
                    }
                } else {
                    logger.warn("Received unknown object type: {}", request.getClass().getName());
                }
            }
        } catch (IOException e) {
            logger.error("IO error in worker thread", e);
        } catch (ClassNotFoundException e) {
            logger.error("Class not found error in worker thread", e);
        } finally {
            logger.info("Worker thread ending");
            cleanup();
        }
    }

    private Response handleRequest(Request request) {
        String handlerName = "handle" + request.getType();
        logger.debug("Handling request: {}", handlerName);

        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            return (Response) method.invoke(this, request);
        } catch (Exception e) {
            logger.error("Error handling request: {}", handlerName, e);
            return new Response.Builder().type(ResponseType.ERROR).data("Error: " + e.getMessage()).build();
        }
    }

    private Response handleLOGIN(Request request) {
        logger.info("Processing LOGIN request");
        String[] creds = (String[]) request.getData();

        try {
            logger.debug("Login attempt: username={}", creds[0]);
            User user = server.login(creds[0], creds[1], this);

            if (user != null) {
                this.user = user;
                logger.info("Login successful: username={}, id={}", user.getUsername(), user.getId());
                return new Response.Builder().type(ResponseType.LOGIN_SUCCESS).data(DTOUtils.getDTO(user)).build();
            } else {
                logger.warn("Login failed for username={}", creds[0]);
                return new Response.Builder().type(ResponseType.LOGIN_FAILURE).data(null).build();
            }
        } catch (BasketballException e) {
            logger.error("Login error", e);
            return new Response.Builder().type(ResponseType.ERROR).data("Login failed: " + e.getMessage()).build();
        }
    }

    private Response handleLOGOUT(Request request) {
        logger.info("Processing LOGOUT request");
        UserDTO userDTO = (UserDTO) request.getData();

        try {
            User user = DTOUtils.getFromDTO(userDTO);
            logger.debug("Logout: username={}, id={}", user.getUsername(), user.getId());

            server.logout(user);
            connected = false;

            logger.info("Logout successful");
            return new Response.Builder().type(ResponseType.OK).data(null).build();
        } catch (BasketballException e) {
            logger.error("Logout error", e);
            return new Response.Builder().type(ResponseType.ERROR).data("Logout failed: " + e.getMessage()).build();
        }
    }

    private Response handleGET_AVAILABLE_MATCHES(Request request) {
        logger.info("Processing GET_AVAILABLE_MATCHES request");

        try {
            logger.debug("Querying available matches");
            Iterable<Match> matches = server.getAvailableMatches();

            MatchDTO[] dtos = StreamSupport.stream(matches.spliterator(), false)
                    .map(DTOUtils::getDTO)
                    .toArray(MatchDTO[]::new);

            logger.info("Found {} available matches", dtos.length);
            return new Response.Builder().type(ResponseType.MATCHES).data(dtos).build();
        } catch (BasketballException e) {
            logger.error("Error fetching matches", e);
            return new Response.Builder().type(ResponseType.ERROR).data("Match fetch failed: " + e.getMessage()).build();
        }
    }

    private Response handleSELL_TICKET(Request request) {
        logger.info("Processing SELL_TICKET request");
        Object data = request.getData();
        logger.debug("Request data type: {}", data != null ? data.getClass().getName() : "null");

        if (!(data instanceof TicketDTO)) {
            logger.warn("Invalid data type for SELL_TICKET request");
            return new Response.Builder().type(ResponseType.ERROR).data("Invalid data for SELL_TICKET").build();
        }

        TicketDTO ticketDTO = (TicketDTO) data;
        Ticket ticket = DTOUtils.getFromDTO(ticketDTO);

        logger.debug("Selling ticket: matchId={}, userId={}, customer={}, seats={}",
                ticket.getMatchId(), ticket.getUserId(), ticket.getCustomerName(), ticket.getSeatsSold());

        try {
            server.sellTicket(ticket.getMatchId(), ticket.getUserId(), ticket.getCustomerName(), ticket.getSeatsSold());
            logger.info("Ticket sold successfully");
            return new Response.Builder().type(ResponseType.TICKET_CONFIRMED).data(null).build();
        } catch (BasketballException e) {
            logger.error("Error selling ticket", e);
            return new Response.Builder().type(ResponseType.ERROR).data("Sell failed: " + e.getMessage()).build();
        }
    }

    @Override
    public void ticketSoldUpdate() throws BasketballException {
        logger.info("Sending ticket sold update to client");

        Response response = new Response.Builder()
                .type(ResponseType.TICKET_SOLD)
                .data(null)
                .build();

        try {
            sendResponse(response);
            logger.debug("Ticket sold update sent");
        } catch (IOException e) {
            logger.error("Error sending ticket sold update", e);
            throw new BasketballException("Error sending TICKET_SOLD update", e);
        }
    }

    private void sendResponse(Response response) throws IOException {
        logger.debug("Sending response: {}", response.getType());

        synchronized (output) {
            output.writeObject(response);
            output.flush();
        }
    }

    private void cleanup() {
        logger.info("Cleaning up worker resources");

        try {
            if (user != null) {
                logger.debug("Logging out user: username={}, id={}", user.getUsername(), user.getId());
                server.logout(user);
            }
        } catch (Exception e) {
            logger.error("Error logging out user during cleanup", e);
        }

        try {
            if (input != null) {
                logger.debug("Closing input stream");
                input.close();
            }

            if (output != null) {
                logger.debug("Closing output stream");
                output.close();
            }

            if (connection != null) {
                logger.debug("Closing socket connection");
                connection.close();
            }

            logger.info("Cleanup complete");
        } catch (IOException e) {
            logger.error("Cleanup error", e);
        }
    }
}