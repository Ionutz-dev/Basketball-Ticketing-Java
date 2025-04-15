package tickets.network;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final List<ClientHandler> clients;
    private PrintWriter out;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    public void sendUpdate(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            out = new PrintWriter(socket.getOutputStream(), true);
            String line;
            while ((line = in.readLine()) != null) {
                // Echo update to all other clients
                for (ClientHandler client : clients) {
                    if (client != this) {
                        client.sendUpdate(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clients.remove(this);
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}
