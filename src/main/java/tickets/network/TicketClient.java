package tickets.network;

import javafx.application.Platform;
import tickets.controller.MainWindowController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class TicketClient extends Thread {
    private final MainWindowController controller;
    private final Socket socket;

    public TicketClient(MainWindowController controller, Socket socket) {
        this.controller = controller;
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while (!socket.isClosed() && (line = in.readLine()) != null) {
                if (line.equals("UPDATE")) {
                    Platform.runLater(controller::loadMatches);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
