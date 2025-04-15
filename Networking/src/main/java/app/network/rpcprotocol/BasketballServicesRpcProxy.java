package app.network.rpcprotocol;

import app.model.Match;
import app.model.User;
import app.services.IBasketballObserver;
import app.services.IBasketballService;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BasketballServicesRpcProxy implements IBasketballService {
    private final String host;
    private final int port;

    private IBasketballObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private final BlockingQueue<Response> responses = new LinkedBlockingQueue<>();
    private volatile boolean finished;

    public BasketballServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect(IBasketballObserver clientObserver) {
        this.client = clientObserver;
        initializeConnection();
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            new Thread(this::runReader).start();
        } catch (Exception e) {
            throw new RuntimeException("Error connecting to server", e);
        }
    }

    private void runReader() {
        while (!finished) {
            try {
                System.out.println(">> Waiting for response...");
                Object response = input.readObject();
                System.out.println(">> Received: " + response);
                if (response instanceof Response resp) {
                    System.out.println("proxy " + resp.getType());
                    if (resp.getType() == ResponseType.TICKET_BOUGHT) {
                        if (client != null) {
                            client.ticketBoughtUpdate(); // no Platform.runLater here
                        }
                    } else {
                        responses.put(resp);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                finished = true;
            }
        }
    }

    private void sendRequest(Request request) throws Exception {
        output.writeObject(request);
        output.flush();
    }

    private Response readResponse() throws Exception {
        return responses.take();
    }

    @Override
    public User login(String username, String password, IBasketballObserver clientObserver) {
        try {
            if (output == null || input == null || connection == null) {
                initializeConnection(); // ✅ ensures streams are set up
            }

            sendRequest(new Request(RequestType.LOGIN, new String[]{username, password}));
            Response response = readResponse();

            if (response.getType() == ResponseType.LOGIN_SUCCESS) {
                return (User) response.getData();
            } else {
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void logout(User user) {
        try {
            sendRequest(new Request(RequestType.LOGOUT, user));
            readResponse();
        } catch (Exception e) {
            System.err.println("Logout failed: " + e.getMessage());
        }
    }

    @Override
    public Iterable<Match> getAvailableMatches() {
        try {
            sendRequest(new Request(RequestType.GET_AVAILABLE_MATCHES, null));
            Response response = readResponse();
            return (Iterable<Match>) response.getData();
        } catch (Exception e) {
            throw new RuntimeException("Fetch failed: " + e.getMessage());
        }
    }

    @Override
    public void buyTicket(int matchId, int userId, String customerName, int seatsToBuy) {
        try {
            sendRequest(new Request(RequestType.BUY_TICKET, new Object[]{matchId, userId, customerName, seatsToBuy}));
            Response response = readResponse();
            if (response.getType() == ResponseType.ERROR) {
                throw new RuntimeException("Purchase failed: " + response.getData());
            }
        } catch (Exception e) {
            throw new RuntimeException("Buy ticket failed: " + e.getMessage());
        }
    }
}
