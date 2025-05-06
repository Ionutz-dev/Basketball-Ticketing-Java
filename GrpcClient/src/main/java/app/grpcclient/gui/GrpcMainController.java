package app.grpcclient.gui;

import app.model.Match;
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
import ticketing.*;

import java.util.Iterator;

public class GrpcMainController {

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

    private io.grpc.Context.CancellableContext watchContext;

    public void setStub(TicketServiceGrpc.TicketServiceBlockingStub stub) {
        this.stub = stub;
    }

    public void setChannel(ManagedChannel channel) {
        this.channel = channel;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(int id) {
        this.userId = id;
    }

    public void initData() {
        welcomeLabel.setText("Logged in as: " + username);

        teamACol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTeamA()));
        teamBCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTeamB()));
        seatsCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getAvailableSeats()).asObject());

        loadMatches();

        TicketServiceGrpc.TicketServiceStub asyncStub = TicketServiceGrpc.newStub(channel);

        asyncStub.watchMatches(TicketProto.Empty.newBuilder().build(), new StreamObserver<TicketProto.MatchList>() {
            @Override
            public void onNext(TicketProto.MatchList update) {
                Platform.runLater(() -> {
                    matchTable.setItems(FXCollections.observableArrayList(
                            update.getMatchesList().stream()
                                    .map(m -> new Match(m.getId(), m.getTeamA(), m.getTeamB(), m.getTicketPrice(), m.getAvailableSeats()))
                                    .toList()
                    ));
                });
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("[WatchMatches] Stream error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("[WatchMatches] Stream completed");
            }
        });
    }

    private void loadMatches() {
        new Thread(() -> {
            try {
                TicketProto.MatchList matchList = stub.getAllMatches(TicketProto.Empty.newBuilder().build());
                Platform.runLater(() -> {
                    matchTable.setItems(FXCollections.observableArrayList(
                            matchList.getMatchesList().stream()
                                    .map(proto -> new Match(proto.getId(), proto.getTeamA(), proto.getTeamB(), proto.getTicketPrice(), proto.getAvailableSeats()))
                                    .toList()
                    ));
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showStatus("Failed to load matches", false));
            }
        }).start();
    }

    @FXML
    private void handleSellTicket() {
        Match selected = matchTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus("Please select a match", false);
            return;
        }

        String customer = customerField.getText();
        String seatsStr = seatsField.getText();

        if (customer.isEmpty() || seatsStr.isEmpty()) {
            showStatus("Fill in all fields", false);
            return;
        }

        try {
            int seats = Integer.parseInt(seatsStr);

            TicketProto.SellTicketResponse response = stub.sellTicket(
                    TicketProto.SellTicketRequest.newBuilder()
                            .setMatchId(selected.getId())
                            .setUserId(userId)
                            .setCustomerName(customer)
                            .setSeats(seats)
                            .build()
            );

            showStatus(response.getMessage(), response.getSuccess());
            loadMatches();
        } catch (NumberFormatException e) {
            showStatus("Invalid seat count", false);
        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Error during ticket purchase", false);
        }
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + (success ? "green" : "red"));
    }

    @FXML
    private void handleLogout() {
        try {
            if (watchContext != null && !watchContext.isCancelled()) {
                watchContext.cancel(null); // Cancel the streaming context
            }

            if (channel != null && !channel.isShutdown()) {
                channel.shutdown();
            }

            // Reload the login window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GrpcLoginWindow.fxml"));
            Parent loginRoot = loader.load();

            GrpcLoginController loginController = loader.getController();
            Stage stage = (Stage) matchTable.getScene().getWindow();
            loginController.setStage(stage); // reuse the same stage

            Scene scene = new Scene(loginRoot);
            stage.setScene(scene);
            stage.setTitle("gRPC Basketball Login");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
