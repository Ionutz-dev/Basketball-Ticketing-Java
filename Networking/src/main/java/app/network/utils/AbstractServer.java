package app.network.utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractServer {
    private final int port;
    private ServerSocket server = null;
    private boolean running = false;

    public AbstractServer(int port) {
        this.port = port;
    }

    public void start() throws ServerException {
        try {
            server = new ServerSocket(port);
            running = true;
            System.out.println("Server started on port " + port);

            while (running) {
                try {
                    Socket client = server.accept();
                    System.out.println("Client connected from " + client.getInetAddress());
                    processRequest(client);
                } catch (IOException e) {
                    throw new ServerException("Error accepting client connection", e);
                }
            }

        } catch (IOException e) {
            throw new ServerException("Error starting server on port " + port, e);
        }
    }

    public void stop() throws ServerException {
        running = false;
        try {
            if (server != null) {
                server.close();
            }
        } catch (IOException e) {
            throw new ServerException("Error closing server", e);
        }
    }

    protected abstract void processRequest(Socket client) throws ServerException;
}
