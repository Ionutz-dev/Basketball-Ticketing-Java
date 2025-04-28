package app.client;

import app.client.gui.LoginController;
import app.client.gui.SceneManager;
import app.network.rpcprotocol.BasketballServicesRpcProxy;
import app.services.IBasketballServices;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartRpcClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        String host = "localhost";
        int port = 55556;

        IBasketballServices service = new BasketballServicesRpcProxy(host, port);

        // Set primary stage globally
        SceneManager.setPrimaryStage(primaryStage);

        // Show LoginWindow scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginWindow.fxml"));
        Scene scene = new Scene(loader.load());

        LoginController controller = loader.getController();
        controller.setService(service);

        primaryStage.setTitle("🏀 Basketball Ticket Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
