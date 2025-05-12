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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ticketing.*;

public class GrpcLoginController {
    private static final Logger logger = LogManager.getLogger(GrpcLoginController.class);

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private int userId;

    private Stage stage;

    public void setStage(Stage stage) {
        logger.debug("Setting stage in GrpcLoginController");
        this.stage = stage;
    }

    public void setUserId(int userId) {
        logger.debug("Setting userId: {}", userId);
        this.userId = userId;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            logger.warn("Empty username or password field");
            Util.showError(stage, "Login Error", "Username and password cannot be empty");
            return;
        }

        logger.info("Login attempt for user: {}", username);

        try {
            logger.debug("Creating gRPC channel to localhost:50051");
            ManagedChannel channel = ManagedChannelBuilder
                    .forAddress("localhost", 50051)
                    .usePlaintext()
                    .build();

            logger.debug("Creating TicketService stub");
            TicketServiceGrpc.TicketServiceBlockingStub stub = TicketServiceGrpc.newBlockingStub(channel);

            // Perform gRPC login
            logger.debug("Sending gRPC login request for user: {}", username);
            TicketProto.LoginResponse response = stub.login(TicketProto.LoginRequest.newBuilder()
                    .setUsername(username)
                    .setPassword(password)
                    .build());

            if (!response.getSuccess()) {
                logger.warn("Login failed: {}", response.getMessage());
                Util.showError(stage, "Login Failed", response.getMessage());
                channel.shutdown();
                return;
            }

            logger.info("Login successful: username={}, userId={}", username, response.getUserId());

            // Load main window on successful login
            logger.debug("Loading GrpcMainWindow.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GrpcMainWindow.fxml"));
            Parent root = loader.load();

            GrpcMainController controller = loader.getController();
            controller.setStub(stub);
            controller.setChannel(channel);
            controller.setUsername(username);
            controller.setUserId(response.getUserId());
            controller.initData();
            logger.debug("GrpcMainController initialized");

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Welcome " + username);
            stage.show();
            logger.info("Main window displayed for user: {}", username);

        } catch (StatusRuntimeException e) {
            logger.error("gRPC status error during login", e);
            Util.showError(stage, "gRPC Error", "Login failed: " + e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error during login", e);
            Util.showError(stage, "Unexpected Error", e.getMessage());
        }
    }
}