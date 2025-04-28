package app.network.utils;

import app.network.rpcprotocol.BasketballClientRpcReflectionWorker;
import app.services.IBasketballServices;

import java.net.Socket;

public class BasketballRpcConcurrentServer extends AbsConcurrentServer {
    private final IBasketballServices service;

    public BasketballRpcConcurrentServer(int port, IBasketballServices service) {
        super(port);
        this.service = service;
        System.out.println("BasketballRpcConcurrentServer initialized on port " + port);
    }

    @Override
    protected Thread createWorker(Socket client) {
        return new Thread(new BasketballClientRpcReflectionWorker(service, client));
    }
}
