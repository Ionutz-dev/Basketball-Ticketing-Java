package app.network.rpcprotocol;

import app.model.Match;
import app.model.User;
import app.services.IBasketballObserver;
import app.services.IBasketballService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BasketballClientRpcWorker extends Thread implements IBasketballObserver {
    private final IBasketballService service;
    private final Socket connection;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private volatile boolean connected = false;

    public BasketballClientRpcWorker(IBasketballService service, Socket connection) {
        this.service = service;
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;

            while (connected) {
                try {
                    Request request = (Request) input.readObject();
                    Response response = handleRequest(request);
                    if (response != null) {
                        sendResponse(response);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    connected = false;
                }
            }
        } catch (IOException e) {
            System.err.println("Error setting up streams: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private Response handleRequest(Request request) {
        switch (request.getType()) {
            case LOGIN -> {
                String[] creds = (String[]) request.getData();
                User user = service.login(creds[0], creds[1], this);
                return new Response(user != null ? ResponseType.LOGIN_SUCCESS : ResponseType.LOGIN_FAILURE, user);
            }
            case GET_AVAILABLE_MATCHES -> {
                Iterable<Match> matches = service.getAvailableMatches();
                return new Response(ResponseType.MATCHES, matches);
            }
            case BUY_TICKET -> {
                Object[] data = (Object[]) request.getData();
                int matchId = (int) data[0];
                int userId = (int) data[1];
                String customerName = (String) data[2];
                int seatsToBuy = (int) data[3];
                service.buyTicket(matchId, userId, customerName, seatsToBuy);
                return new Response(ResponseType.TICKET_CONFIRMED, null);
            }
            case LOGOUT -> {
                User user = (User) request.getData();
                service.logout(user);
                connected = false;
                return new Response(ResponseType.OK, null);
            }
            default -> {
                return new Response(ResponseType.ERROR, "Unknown request");
            }
        }
    }

    public void sendPushUpdate() {
        sendResponse(new Response(ResponseType.TICKET_BOUGHT, null));
    }

    private void sendResponse(Response response) {
        try {
            synchronized (output) {
                output.writeObject(response);
                output.flush();
                output.reset(); // Optional: avoid caching issues
            }
        } catch (IOException e) {
            System.err.println("Error sending response to client: " + e.getMessage());
        }
    }

    @Override
    public void ticketBoughtUpdate() {
        sendPushUpdate();
    }

    private void cleanup() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (connection != null) connection.close();
        } catch (IOException e) {
            System.err.println("Cleanup error: " + e.getMessage());
        }
    }
}
