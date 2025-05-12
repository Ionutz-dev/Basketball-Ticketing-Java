package app.client.gui;

import app.model.User;
import app.network.rpcprotocol.BasketballServicesRpcProxy;
import app.services.BasketballException;
import app.services.IBasketballServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static app.client.gui.Util.showError;

public class LoginController {
    private static final Logger logger = LogManager.getLogger(LoginController.class);

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private IBasketballServices service;

    public void setService(IBasketballServices service) {
        logger.debug("Setting service in LoginController");
        this.service = service;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            logger.warn("Empty username or password field");
            showError(SceneManager.getPrimaryStage(), "Login Error", "Username and password cannot be empty");
            return;
        }

        logger.info("Login attempt for user: {}", username);

        try {
            logger.debug("Creating new RPC proxy for login");
            IBasketballServices newService = new BasketballServicesRpcProxy("localhost", 55556);

            logger.debug("Loading MainWindow.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainWindow.fxml"));
            Parent root = loader.load();
            MainController mainController = loader.getController();

            logger.debug("Performing login operation");
            User user = newService.login(username, password, mainController);

            if (user == null) {
                logger.warn("Login failed for user: {}", username);
                showError(SceneManager.getPrimaryStage(), "Login failed", "Invalid username or password.");
                return;
            }

            logger.info("Login successful for user: {}, id: {}", username, user.getId());
            mainController.setService(newService);
            mainController.setLoggedUser(user);
            mainController.setStageCloseEvent();
            logger.debug("MainController initialized with user and service");

            Scene scene = new Scene(root);
            Stage stage = SceneManager.getPrimaryStage();
            stage.setTitle("🏀 Welcome " + user.getUsername());
            stage.setScene(scene);
            stage.show();
            logger.info("Main window displayed for user: {}", username);

        } catch (BasketballException e) {
            logger.error("BasketballException during login", e);
            showError(SceneManager.getPrimaryStage(), "Login Error", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during login", e);
            showError(SceneManager.getPrimaryStage(), "Unexpected Error", e.getMessage());
        }
    }
}