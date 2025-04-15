package app.client.gui;

import app.model.Match;
import app.model.User;
import app.services.IBasketballObserver;
import app.services.IBasketballService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class MainController implements IBasketballObserver {

    private IBasketballService service;
    private User loggedUser;

    @FXML
    private TableView<Match> matchTable;

    @FXML
    private TableColumn<Match, String> team1Column;
    @FXML
    private TableColumn<Match, String> team2Column;
    @FXML
    private TableColumn<Match, Integer> availableSeatsColumn;

    @FXML
    private TextField seatsField;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label statusLabel;

    public void setService(IBasketballService service) {
        this.service = service;
        initTable();
        loadMatchData();
    }

    public void setLoggedUser(User user) {
        this.loggedUser = user;
        welcomeLabel.setText("Logged in as: " + user.getUsername());
    }

    private void initTable() {
        team1Column.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("teamA"));
        team2Column.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("teamB"));
        availableSeatsColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("availableSeats"));
    }

    public void loadMatchData() {
        try {
            Iterable<Match> matches = service.getAvailableMatches();
            List<Match> matchList = new ArrayList<>();
            matches.forEach(matchList::add);
            matchTable.setItems(FXCollections.observableArrayList(matchList));
        } catch (Exception e) {
            System.err.println("Failed to load matches: " + e.getMessage());
        }
    }

    @FXML
    private void handleBuyTicket() {
        Match selectedMatch = matchTable.getSelectionModel().getSelectedItem();
        if (selectedMatch == null) {
            showStatus("Please select a match.", false);
            return;
        }

        String seatsText = seatsField.getText();
        if (seatsText.isEmpty()) {
            showStatus("Enter number of seats to buy.", false);
            return;
        }

        try {
            int seats = Integer.parseInt(seatsText);
            if (seats <= 0) {
                showStatus("Number of seats must be positive.", false);
                return;
            }

            if (seats > selectedMatch.getAvailableSeats()) {
                showStatus("Not enough seats available.", false);
                return;
            }

            service.buyTicket(selectedMatch.getId(), loggedUser.getId(), loggedUser.getUsername(), seats);
            showStatus("Ticket bought successfully!", true);
            seatsField.clear();
            loadMatchData();

        } catch (NumberFormatException e) {
            showStatus("Please enter a valid number.", false);
        } catch (Exception e) {
            showStatus("Purchase failed: " + e.getMessage(), false);
        }
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + (success ? "green" : "red") + "; -fx-font-size: 13px;");
    }

    @Override
    public void ticketBoughtUpdate() {
        Platform.runLater(() -> {
            loadMatchData();
            statusLabel.setText("Ticket bought successfully!");
        });
    }
}
