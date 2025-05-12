package app.network.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

public abstract class AbsConcurrentServer extends AbstractServer {
    private static final Logger logger = LogManager.getLogger(AbsConcurrentServer.class);

    public AbsConcurrentServer(int port) {
        super(port);
        logger.info("Concurrent AbstractServer created on port {}", port);
    }

    @Override
    protected void processRequest(Socket client) {
        logger.debug("Creating worker thread for client: {}", client.getRemoteSocketAddress());
        Thread worker = createWorker(client);
        worker.start();
        logger.debug("Worker thread started for client: {}", client.getRemoteSocketAddress());
    }

    protected abstract Thread createWorker(Socket client);
}