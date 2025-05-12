package app.network.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractServer {
    private static final Logger logger = LogManager.getLogger(AbstractServer.class);

    private final int port;
    private ServerSocket server = null;
    private boolean running = false;

    public AbstractServer(int port) {
        logger.debug("Creating AbstractServer on port {}", port);
        this.port = port;
    }

    public void start() throws ServerException {
        logger.info("Starting AbstractServer on port {}", port);

        try {
            server = new ServerSocket(port);
            running = true;
            logger.info("Server socket created and listening on port {}", port);

            while (running) {
                logger.debug("Waiting for client connection");
                try {
                    Socket client = server.accept();
                    logger.info("Client connected from {}", client.getInetAddress());
                    processRequest(client);
                } catch (IOException e) {
                    if (running) {
                        logger.error("Error accepting client connection", e);
                        throw new ServerException("Error accepting client connection", e);
                    } else {
                        logger.debug("Server was stopped, ignoring accept exception");
                    }
                }
            }

        } catch (IOException e) {
            logger.error("Error starting server on port {}", port, e);
            throw new ServerException("Error starting server on port " + port, e);
        } finally {
            logger.debug("Server main loop ended");
        }
    }

    public void stop() throws ServerException {
        logger.info("Stopping AbstractServer");
        running = false;
        try {
            if (server != null) {
                server.close();
                logger.debug("Server socket closed");
            }
            logger.info("Server stopped");
        } catch (IOException e) {
            logger.error("Error closing server socket", e);
            throw new ServerException("Error closing server", e);
        }
    }

    protected abstract void processRequest(Socket client) throws ServerException;
}