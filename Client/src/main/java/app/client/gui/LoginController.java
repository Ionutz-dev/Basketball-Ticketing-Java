package app.client.gui;

import app.model.User;
import app.network.rpcprotocol.BasketballServicesRpcProxy;
import app.services.IBasketballObserver;
import app.services.IBasketballService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private IBasketballService service;
    private Stage primaryStage;

    public void setService(IBasketballService service) {
        this.service = service;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
            Scene scene = new Scene(loader.load());
            MainController controller = loader.getController();

            User user = null;

            if (service instanceof BasketballServicesRpcProxy proxy) {
                proxy.connect(controller); // ✅ Establish connection first
                user = service.login(username, password, controller); // ✅ Login after connection
            }

            if (user == null) {
                showError("Login failed", "Invalid credentials.");
                return;
            }

            controller.setLoggedUser(user);
            controller.setService(service); // ✅ Set service after connection is ready

            primaryStage.setTitle("Welcome, " + user.getUsername());
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(primaryStage);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
