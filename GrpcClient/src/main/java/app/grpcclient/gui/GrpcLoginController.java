package app.grpcclient.gui;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ticketing.*;

public class GrpcLoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private int userId;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            ManagedChannel channel = ManagedChannelBuilder
                    .forAddress("localhost", 50051)
                    .usePlaintext()
                    .build();

            TicketServiceGrpc.TicketServiceBlockingStub stub = TicketServiceGrpc.newBlockingStub(channel);

            // Perform gRPC login
            TicketProto.LoginResponse response = stub.login(TicketProto.LoginRequest.newBuilder()
                    .setUsername(username)
                    .setPassword(password)
                    .build());

            if (!response.getSuccess()) {
                System.err.println("Login failed: " + response.getMessage());
                channel.shutdown();
                return;
            }

            // Load main window on successful login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GrpcMainWindow.fxml"));
            Parent root = loader.load();

            GrpcMainController controller = loader.getController();
            controller.setStub(stub);
            controller.setChannel(channel);
            controller.setUsername(username);
            controller.setUserId(response.getUserId());
            controller.initData();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Welcome " + username);
            stage.show();

        } catch (StatusRuntimeException e) {
            System.err.println("gRPC login failed: " + e.getStatus());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
