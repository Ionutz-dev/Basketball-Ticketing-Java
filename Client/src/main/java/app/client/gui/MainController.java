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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class MainController implements IBasketballObserver {
    private static final Logger logger = LogManager.getLogger(MainController.class);

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
        logger.debug("Setting service in MainController");
        this.service = service;
        initTable();
        loadMatchData();
    }

    public void setLoggedUser(User user) {
        logger.debug("Setting logged user: {}, id: {}", user.getUsername(), user.getId());
        this.loggedUser = user;
        welcomeLabel.setText("Logged in as: " + user.getUsername());
    }

    public void setStageCloseEvent() {
        logger.debug("Setting stage close event");
        Stage stage = SceneManager.getPrimaryStage();
        stage.setOnCloseRequest(event -> {
            logger.info("Window close requested - handling logout");
            event.consume();
            handleLogout();
        });
    }

    private void initTable() {
        logger.debug("Initializing match table");
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
        logger.debug("Match table initialized");
    }

    public void loadMatchData() {
        logger.info("Loading match data");
        new Thread(() -> {
            try {
                logger.debug("Fetching available matches from service");
                Iterable<Match> matches = service.getAvailableMatches();
                List<Match> matchList = new ArrayList<>();
                matches.forEach(matchList::add);
                logger.debug("Received {} matches", matchList.size());

                Platform.runLater(() -> {
                    matchTable.setItems(FXCollections.observableArrayList(matchList));
                    logger.debug("Match table updated with data");
                });

            } catch (BasketballException e) {
                logger.error("Error loading matches", e);
                Platform.runLater(() -> showStatus("Failed to load matches: " + e.getMessage(), false));
            }
        }).start();
    }

    @FXML
    private void handleSellTicket() {
        logger.info("Sell ticket operation requested");
        new Thread(() -> {
            Match selectedMatch = matchTable.getSelectionModel().getSelectedItem();
            if (selectedMatch == null) {
                logger.warn("No match selected for ticket sale");
                Platform.runLater(() -> showStatus("Please select a match.", false));
                return;
            }

            String customer = customerNameField.getText();
            String seatsText = seatsField.getText();

            if (customer.isEmpty() || seatsText.isEmpty()) {
                logger.warn("Customer name or seats count not provided");
                Platform.runLater(() -> showStatus("Enter customer name and seat count.", false));
                return;
            }

            try {
                int seats = Integer.parseInt(seatsText);
                if (seats <= 0) {
                    logger.warn("Invalid number of seats: {}", seats);
                    Platform.runLater(() -> showStatus("Number of seats must be positive.", false));
                    return;
                }

                if (seats > selectedMatch.getAvailableSeats()) {
                    logger.warn("Not enough seats available: requested={}, available={}",
                            seats, selectedMatch.getAvailableSeats());
                    Platform.runLater(() -> showStatus("Not enough seats available.", false));
                    return;
                }

                logger.debug("Selling ticket: matchId={}, userId={}, customer={}, seats={}",
                        selectedMatch.getId(), loggedUser.getId(), customer, seats);

                service.sellTicket(selectedMatch.getId(), loggedUser.getId(), customer, seats);

                logger.info("Ticket sold successfully: matchId={}, customer={}, seats={}",
                        selectedMatch.getId(), customer, seats);

                Platform.runLater(() -> {
                    loadMatchData();
                    showStatus("Ticket sold to " + customer + "!", true);
                    customerNameField.clear();
                    seatsField.clear();
                });

            } catch (NumberFormatException e) {
                logger.warn("Invalid number format for seats: {}", seatsText);
                Platform.runLater(() -> showStatus("Invalid number format.", false));
            } catch (BasketballException e) {
                logger.error("Error during ticket sale", e);
                Platform.runLater(() -> showStatus("Sale failed: " + e.getMessage(), false));
            }
        }).start();
    }

    @FXML
    private void handleLogout() {
        logger.info("Logout requested for user: {}", loggedUser != null ? loggedUser.getUsername() : "null");
        try {
            if (loggedUser != null) {
                logger.debug("Performing logout for user: {}", loggedUser.getUsername());
                service.logout(loggedUser);
            }
        } catch (BasketballException e) {
            logger.error("Error during logout", e);
            showStatus("Logout error: " + e.getMessage(), false);
        }

        try {
            logger.debug("Loading login window");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginWindow.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();

            logger.debug("Creating new RPC proxy for login window");
            IBasketballServices newService = new BasketballServicesRpcProxy("localhost", 55556);
            loginController.setService(newService);

            Scene scene = new Scene(root);
            Stage stage = SceneManager.getPrimaryStage();
            stage.setTitle("🏀 Basketball Ticket Login");
            stage.setScene(scene);
            stage.show();

            // Remove the close event so that LoginWindow closes normally
            stage.setOnCloseRequest(null);
            logger.info("Returned to login screen");

        } catch (Exception e) {
            logger.error("Error returning to login screen", e);
        }
    }

    private void showStatus(String message, boolean success) {
        logger.debug("Showing status message: '{}', success={}", message, success);
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + (success ? "green" : "red") + "; -fx-font-size: 13px;");
    }

    @Override
    public void ticketSoldUpdate() throws BasketballException {
        logger.info("Received ticketSoldUpdate notification on thread: {}", Thread.currentThread().getName());
        Platform.runLater(() -> {
            logger.debug("Updating matches on JavaFX thread: {}", Thread.currentThread().getName());
            loadMatchData();
        });
    }
}