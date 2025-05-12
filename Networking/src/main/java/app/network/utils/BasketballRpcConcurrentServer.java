package app.network.utils;

import app.network.rpcprotocol.BasketballClientRpcReflectionWorker;
import app.services.IBasketballServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

public class BasketballRpcConcurrentServer extends AbsConcurrentServer {
    private static final Logger logger = LogManager.getLogger(BasketballRpcConcurrentServer.class);
    private final IBasketballServices service;

    public BasketballRpcConcurrentServer(int port, IBasketballServices service) {
        super(port);
        this.service = service;
        logger.info("BasketballRpcConcurrentServer initialized on port {}", port);
    }

    @Override
    protected Thread createWorker(Socket client) {
        logger.debug("Creating worker for client: {}", client.getRemoteSocketAddress());
        BasketballClientRpcReflectionWorker worker = new BasketballClientRpcReflectionWorker(service, client);
        Thread workerThread = new Thread(worker);
        logger.debug("Worker thread created for client: {}", client.getRemoteSocketAddress());
        return workerThread;
    }
}