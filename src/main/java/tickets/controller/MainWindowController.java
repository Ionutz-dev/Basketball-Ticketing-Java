package tickets.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tickets.model.Match;
import tickets.repository.MatchRepository;
import tickets.repository.TicketRepository;
import tickets.service.TicketService;
import tickets.model.User;
import tickets.network.TicketClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MainWindowController {
    @FXML
    private TableView<Match> matchTable;
    @FXML
    private TableColumn<Match, String> teamColumn;
    @FXML
    private TableColumn<Match, Double> priceColumn;
    @FXML
    private TableColumn<Match, Integer> seatsColumn;
    @FXML
    private TextField seatsField;
    @FXML
    private Label statusLabel;

    private User currentUser;
    private TicketService ticketService;
    private TicketClient ticketClient;
    private Socket socket;

    // Setter for login controller to call
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        ticketService = new TicketService(new MatchRepository(), new TicketRepository());

        teamColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTeamA() + " vs " + data.getValue().getTeamB()));
        priceColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getTicketPrice()));
        seatsColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getAvailableSeats()));

        loadMatches(); // only load data
    }

    public void loadMatches() {
        ObservableList<Match> matchList = FXCollections.observableArrayList();
        ticketService.getAvailableMatches().forEach(matchList::add);
        matchTable.setItems(matchList);
    }

    @FXML
    private void handleBuyTicket() {
        Match selectedMatch = matchTable.getSelectionModel().getSelectedItem();
        String seatsText = seatsField.getText().trim();

        if (selectedMatch == null) {
            statusLabel.setText("Please select a match.");
            return;
        }

        if (seatsText.isEmpty()) {
            statusLabel.setText("Enter number of seats.");
            return;
        }

        int seats;
        try {
            seats = Integer.parseInt(seatsText);
            if (seats <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid number of seats.");
            return;
        }

        if (selectedMatch.getAvailableSeats() < seats) {
            statusLabel.setText("Not enough available seats.");
            return;
        }

        // Use username from the logged-in user
        String customer = currentUser.getUsername();

        ticketService.buyTicket(selectedMatch.getId(), currentUser.getId(), customer, seats);
        statusLabel.setText("Ticket sold successfully");

        // Notify others
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("UPDATE");
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadMatches();
        seatsField.clear();
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            ticketClient = new TicketClient(this, socket);
            ticketClient.start();
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Connection to server failed.");
        }
    }

    public void cleanup() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
