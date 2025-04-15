package tickets;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tickets.controller.LoginController;
import tickets.controller.MainWindowController;
import tickets.model.User;

import java.io.IOException;
import java.net.Socket;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Show login screen
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent loginRoot = loginLoader.load();
        LoginController loginController = loginLoader.getController();

        Stage loginStage = new Stage();
        loginStage.setTitle("Login");
        loginStage.setScene(new Scene(loginRoot));
        loginStage.showAndWait();

        User loggedInUser = loginController.getLoggedInUser();
        if (loggedInUser == null) {
            System.exit(0); // User canceled login
            return;
        }

        // Now load the main window
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/MainWindow.fxml"));
        Parent mainRoot = mainLoader.load();
        MainWindowController mainCtrl = mainLoader.getController();
        mainCtrl.setCurrentUser(loggedInUser);

        Socket socket = new Socket("localhost", 5555);
        mainCtrl.setSocket(socket);

        primaryStage.setTitle("Basketball Ticket Sales");
        primaryStage.setScene(new Scene(mainRoot));
        primaryStage.setOnCloseRequest(e -> mainCtrl.cleanup()); // Graceful shutdown
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
