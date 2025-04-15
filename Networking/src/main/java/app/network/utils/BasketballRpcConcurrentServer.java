package app.network.utils;

import app.network.rpcprotocol.BasketballClientRpcWorker;
import app.services.IBasketballService;

import java.net.Socket;

public class BasketballRpcConcurrentServer extends AbsConcurrentServer {
    private final IBasketballService service;

    public BasketballRpcConcurrentServer(int port, IBasketballService service) {
        super(port);
        this.service = service;
        System.out.println("✅ BasketballRpcConcurrentServer initialized on port " + port);
    }

    @Override
    protected Thread createWorker(Socket client) {
        BasketballClientRpcWorker worker = new BasketballClientRpcWorker(service, client);
        return new Thread(worker);
    }
}
