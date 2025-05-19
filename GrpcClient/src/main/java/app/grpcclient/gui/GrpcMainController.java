package app.grpcclient.gui;

import app.model.Match;
import io.grpc.Context;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ticketing.*;

import java.util.List;
import java.util.stream.Collectors;

public class GrpcMainController {
    private static final Logger logger = LogManager.getLogger(GrpcMainController.class);

    private TicketServiceGrpc.TicketServiceBlockingStub stub;
    private ManagedChannel channel;
    private String username;
    private int userId;

    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<Match> matchTable;

    @FXML
    private TableColumn<Match, String> teamACol;
    @FXML
    private TableColumn<Match, String> teamBCol;
    @FXML
    private TableColumn<Match, Integer> seatsCol;

    @FXML
    private TextField customerField;

    @FXML
    private TextField seatsField;

    @FXML
    private Label statusLabel;

    private Context.CancellableContext watchContext;

    public void setStub(TicketServiceGrpc.TicketServiceBlockingStub stub) {
        logger.debug("Setting gRPC stub in GrpcMainController");
        this.stub = stub;
    }

    public void setChannel(ManagedChannel channel) {
        logger.debug("Setting gRPC channel in GrpcMainController");
        this.channel = channel;
    }

    public void setUsername(String username) {
        logger.debug("Setting username: {}", username);
        this.username = username;
    }

    public void setUserId(int id) {
        logger.debug("Setting userId: {}", id);
        this.userId = id;
    }

    public void initData() {
        logger.info("Initializing GrpcMainController data");
        welcomeLabel.setText("Logged in as: " + username);

        logger.debug("Setting up table columns");
        teamACol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTeamA()));
        teamBCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTeamB()));
        seatsCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getAvailableSeats()).asObject());

        loadMatches();
        setupMatchesObserver();
    }

    private void setupMatchesObserver() {
        logger.debug("Setting up matches stream observer");
        TicketServiceGrpc.TicketServiceStub asyncStub = TicketServiceGrpc.newStub(channel);

        asyncStub.watchMatches(TicketProto.Empty.newBuilder().build(), new StreamObserver<TicketProto.MatchList>() {
            @Override
            public void onNext(TicketProto.MatchList update) {
                logger.debug("Received match updates from stream, count: {}", update.getMatchesCount());
                Platform.runLater(() -> {
                    List<Match> matchList = update.getMatchesList().stream()
                            .map(m -> new Match(m.getId(), m.getTeamA(), m.getTeamB(), m.getTicketPrice(), m.getAvailableSeats()))
                            .collect(Collectors.toList());

                    matchTable.setItems(FXCollections.observableArrayList(matchList));
                    logger.trace("Updated match table with stream data");
                });
            }

            @Override
            public void onError(Throwable t) {
                logger.error("[WatchMatches] Stream error", t);
            }

            @Override
            public void onCompleted() {
                logger.info("[WatchMatches] Stream completed");
            }
        });

        logger.debug("Match stream observer setup complete");
    }

    private void loadMatches() {
        logger.info("Loading matches data");
        new Thread(() -> {
            try {
                logger.debug("Sending gRPC request for all matches");
                TicketProto.MatchList matchList = stub.getAllMatches(TicketProto.Empty.newBuilder().build());
                logger.debug("Received {} matches", matchList.getMatchesCount());

                List<Match> matches = matchList.getMatchesList().stream()
                        .map(proto -> new Match(proto.getId(), proto.getTeamA(), proto.getTeamB(),
                                proto.getTicketPrice(), proto.getAvailableSeats()))
                        .collect(Collectors.toList());

                Platform.runLater(() -> {
                    matchTable.setItems(FXCollections.observableArrayList(matches));
                    logger.debug("Match table updated with data");
                });
            } catch (Exception e) {
                logger.error("Error loading matches", e);
                Platform.runLater(() -> showStatus("Failed to load matches", false));
            }
        }).start();
    }

    @FXML
    private void handleSellTicket() {
        logger.info("Sell ticket operation requested");
        Match selected = matchTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            logger.warn("No match selected for ticket sale");
            showStatus("Please select a match", false);
            return;
        }

        String customer = customerField.getText();
        String seatsStr = seatsField.getText();

        if (customer.isEmpty() || seatsStr.isEmpty()) {
            logger.warn("Customer name or seats count not provided");
            showStatus("Fill in all fields", false);
            return;
        }

        try {
            int seats = Integer.parseInt(seatsStr);
            if (seats <= 0) {
                logger.warn("Invalid number of seats: {}", seats);
                showStatus("Number of seats must be positive", false);
                return;
            }

            if (seats > selected.getAvailableSeats()) {
                logger.warn("Not enough seats available: requested={}, available={}",
                        seats, selected.getAvailableSeats());
                showStatus("Not enough seats available", false);
                return;
            }

            logger.debug("Selling ticket: matchId={}, userId={}, customer={}, seats={}",
                    selected.getId(), userId, customer, seats);

            TicketProto.SellTicketResponse response = stub.sellTicket(
                    TicketProto.SellTicketRequest.newBuilder()
                            .setMatchId(selected.getId())
                            .setUserId(userId)
                            .setCustomerName(customer)
                            .setSeats(seats)
                            .build()
            );

            if (response.getSuccess()) {
                logger.info("Ticket sold successfully: matchId={}, customer={}, seats={}",
                        selected.getId(), customer, seats);
                showStatus(response.getMessage(), true);
                customerField.clear();
                seatsField.clear();
            } else {
                logger.warn("Ticket sale failed: {}", response.getMessage());
                showStatus(response.getMessage(), false);
            }

            loadMatches();
        } catch (NumberFormatException e) {
            logger.warn("Invalid seat count format: {}", seatsStr);
            showStatus("Invalid seat count", false);
        } catch (Exception e) {
            logger.error("Error during ticket purchase", e);
            showStatus("Error during ticket purchase", false);
        }
    }

    private void showStatus(String message, boolean success) {
        logger.debug("Showing status message: '{}', success={}", message, success);
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + (success ? "green" : "red"));
    }

    @FXML
    private void handleLogout() {
        logger.info("Logout requested for user: {}", username);
        try {
            if (watchContext != null && !watchContext.isCancelled()) {
                logger.debug("Cancelling stream context");
                watchContext.cancel(null); // Cancel the streaming context
            }

            if (channel != null && !channel.isShutdown()) {
                logger.debug("Shutting down gRPC channel");
                channel.shutdown();
            }

            // Reload the login window
            logger.debug("Loading GrpcLoginWindow.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GrpcLoginWindow.fxml"));
            Parent loginRoot = loader.load();

            GrpcLoginController loginController = loader.getController();
            Stage stage = (Stage) matchTable.getScene().getWindow();
            loginController.setStage(stage);

            Scene scene = new Scene(loginRoot);
            stage.setScene(scene);
            stage.setTitle("gRPC Basketball Login");
            stage.show();
            logger.info("Returned to login screen");

        } catch (Exception e) {
            logger.error("Error during logout", e);
            showStatus("Error during logout", false);
        }
    }
}