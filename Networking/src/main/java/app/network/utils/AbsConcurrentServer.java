package app.network.utils;

import java.net.Socket;

public abstract class AbsConcurrentServer extends AbstractServer {

    public AbsConcurrentServer(int port) {
        super(port);
        System.out.println("Concurrent AbstractServer created");
    }

    @Override
    protected void processRequest(Socket client) {
        Thread worker = createWorker(client);
        worker.start();
    }

    protected abstract Thread createWorker(Socket client);
}
