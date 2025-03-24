package tickets.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tickets.model.Match;
import tickets.model.User;
import tickets.repository.MatchRepository;
import tickets.repository.TicketRepository;
import tickets.service.TicketService;

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
    private TextField customerField;
    @FXML
    private TextField seatsField;
    @FXML
    private Label statusLabel;

    private User currentUser;
    private ObservableList<Match> matchModel = FXCollections.observableArrayList();
    private TicketService ticketService;

    // Setter for login controller to call
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        // TEMP: Hardcoded test user
        this.currentUser = new User(1, "testuser", "password");  // Example constructor

        // Initialize service with repository implementations
        MatchRepository matchRepo = new MatchRepository();
        TicketRepository ticketRepo = new TicketRepository();
        ticketService = new TicketService(matchRepo, ticketRepo);

        // Set up table columns
        teamColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTeamA() + " vs " + data.getValue().getTeamB())
        );
        priceColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTicketPrice())
        );
        seatsColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAvailableSeats())
        );

        loadMatches();
    }

    private void loadMatches() {
        matchModel.clear();
        ticketService.getAvailableMatches().forEach(matchModel::add);
        matchTable.setItems(matchModel);
    }

    @FXML
    private void handleBuyTicket() {
        Match selectedMatch = matchTable.getSelectionModel().getSelectedItem();
        String customer = customerField.getText().trim();
        String seatsText = seatsField.getText().trim();

        if (selectedMatch == null) {
            statusLabel.setText("Please select a match.");
            return;
        }

        if (currentUser == null) {
            statusLabel.setText("No logged-in user.");
            return;
        }

        if (customer.isEmpty() || seatsText.isEmpty()) {
            statusLabel.setText("Enter customer name and number of seats.");
            return;
        }

        int seats;
        try {
            seats = Integer.parseInt(seatsText);
            if (seats <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            statusLabel.setText("Enter a valid number of seats.");
            return;
        }

        if (selectedMatch.getAvailableSeats() < seats) {
            statusLabel.setText("Not enough seats available.");
            return;
        }

        // Process ticket sale
        ticketService.buyTicket(selectedMatch.getId(), currentUser.getId(), customer, seats);
        statusLabel.setText("Ticket sold successfully");

        // Refresh matches in table
        loadMatches();

        // Clear input fields
        customerField.clear();
        seatsField.clear();
    }
}
