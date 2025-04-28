package app.client.gui;

import app.model.Match;
import app.model.User;
import app.network.rpcprotocol.BasketballServicesRpcProxy;
import app.services.BasketballException;
import app.services.IBasketballObserver;
import app.services.IBasketballServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class MainController implements IBasketballObserver {

    private IBasketballServices service;
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
    private TextField customerNameField;
    @FXML
    private TextField seatsField;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label statusLabel;

    public void setService(IBasketballServices service) {
        this.service = service;
        initTable();
        loadMatchData();
    }

    public void setLoggedUser(User user) {
        this.loggedUser = user;
        welcomeLabel.setText("Logged in as: " + user.getUsername());
    }

    public void setStageCloseEvent() {
        Stage stage = SceneManager.getPrimaryStage();
        stage.setOnCloseRequest(event -> {
            event.consume();

            handleLogout();
        });
    }

    private void initTable() {
        team1Column.setCellValueFactory(new PropertyValueFactory<>("teamA"));
        team2Column.setCellValueFactory(new PropertyValueFactory<>("teamB"));
        availableSeatsColumn.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));

        availableSeatsColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Match, Integer> call(TableColumn<Match, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer seats, boolean empty) {
                        super.updateItem(seats, empty);
                        if (empty || seats == null) {
                            setText(null);
                            setStyle("");
                        } else if (seats == 0) {
                            setText("SOLD OUT");
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        } else {
                            setText(seats.toString());
                            setStyle("");
                        }
                    }
                };
            }
        });
    }

    public void loadMatchData() {
        new Thread(() -> {
            try {
                Iterable<Match> matches = service.getAvailableMatches();
                List<Match> matchList = new ArrayList<>();
                matches.forEach(matchList::add);

                Platform.runLater(() -> {
                    matchTable.setItems(FXCollections.observableArrayList(matchList));
                });

            } catch (BasketballException e) {
                e.printStackTrace();
                Platform.runLater(() -> showStatus("Failed to load matches: " + e.getMessage(), false));
            }
        }).start();
    }

    @FXML
    private void handleSellTicket() {
        new Thread(() -> {
            Match selectedMatch = matchTable.getSelectionModel().getSelectedItem();
            if (selectedMatch == null) {
                Platform.runLater(() -> showStatus("Please select a match.", false));
                return;
            }

            String customer = customerNameField.getText();
            String seatsText = seatsField.getText();

            if (customer.isEmpty() || seatsText.isEmpty()) {
                Platform.runLater(() -> showStatus("Enter customer name and seat count.", false));
                return;
            }

            try {
                int seats = Integer.parseInt(seatsText);
                if (seats <= 0) {
                    Platform.runLater(() -> showStatus("Number of seats must be positive.", false));
                    return;
                }

                if (seats > selectedMatch.getAvailableSeats()) {
                    Platform.runLater(() -> showStatus("Not enough seats available.", false));
                    return;
                }

                service.sellTicket(selectedMatch.getId(), loggedUser.getId(), customer, seats);

                Platform.runLater(() -> {
                    loadMatchData();
                    showStatus("Ticket sold to " + customer + "!", true);
                    customerNameField.clear();
                    seatsField.clear();
                });

            } catch (NumberFormatException e) {
                Platform.runLater(() -> showStatus("Invalid number format.", false));
            } catch (BasketballException e) {
                Platform.runLater(() -> showStatus("Sale failed: " + e.getMessage(), false));
            }
        }).start();
    }

    @FXML
    private void handleLogout() {
        try {
            if (loggedUser != null) {
                service.logout(loggedUser);
            }
        } catch (BasketballException e) {
            e.printStackTrace();
            showStatus("Logout error: " + e.getMessage(), false);
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginWindow.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();

            IBasketballServices newService = new BasketballServicesRpcProxy("localhost", 55556);
            loginController.setService(newService);

            Scene scene = new Scene(root);
            Stage stage = SceneManager.getPrimaryStage();
            stage.setTitle("🏀 Basketball Ticket Login");
            stage.setScene(scene);
            stage.show();

            // Remove the close event so that LoginWindow closes normally
            stage.setOnCloseRequest(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + (success ? "green" : "red") + "; -fx-font-size: 13px;");
    }

    @Override
    public void ticketSoldUpdate() {
        System.out.println("[Controller] ticketSoldUpdate() called on thread: " + Thread.currentThread().getName());
        Platform.runLater(() -> {
            System.out.println("[Controller] Updating matches on thread: " + Thread.currentThread().getName());
            loadMatchData();
        });
    }
}
