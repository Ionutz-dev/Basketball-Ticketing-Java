package tickets.controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import tickets.model.User;
import tickets.repository.IUserRepository;
import tickets.repository.UserRepository;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private IUserRepository userRepo = new UserRepository();
    private User loggedInUser;

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Both fields are required.");
            return;
        }

        User user = userRepo.findByUsernameAndPassword(username, password);
        if (user != null) {
            loggedInUser = user;
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.close();
        } else {
            showAlert("Login Failed", "Invalid credentials.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
