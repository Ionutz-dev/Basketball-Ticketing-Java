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

import static app.client.gui.Util.showError;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private IBasketballServices service;

    public void setService(IBasketballServices service) {
        this.service = service;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            IBasketballServices newService = new BasketballServicesRpcProxy("localhost", 55556);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainWindow.fxml"));
            Parent root = loader.load();
            MainController mainController = loader.getController();

            User user = newService.login(username, password, mainController);

            if (user == null) {
                showError(SceneManager.getPrimaryStage(), "Login failed", "Invalid username or password.");
                return;
            }

            mainController.setService(newService);
            mainController.setLoggedUser(user);
            mainController.setStageCloseEvent();

            Scene scene = new Scene(root);
            Stage stage = SceneManager.getPrimaryStage();
            stage.setTitle("🏀 Welcome " + user.getUsername());
            stage.setScene(scene);
            stage.show();

        } catch (BasketballException e) {
            showError(SceneManager.getPrimaryStage(), "Login Error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showError(SceneManager.getPrimaryStage(), "Unexpected Error", e.getMessage());
        }
    }
}
