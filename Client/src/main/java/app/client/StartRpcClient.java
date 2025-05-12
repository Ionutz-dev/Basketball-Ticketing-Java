package app.client;

import app.client.gui.LoginController;
import app.client.gui.SceneManager;
import app.network.rpcprotocol.BasketballServicesRpcProxy;
import app.services.IBasketballServices;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StartRpcClient extends Application {
    private static final Logger logger = LogManager.getLogger(StartRpcClient.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Starting RPC Client application");

        String host = "localhost";
        int port = 55556;
        logger.debug("Connecting to server at {}:{}", host, port);

        try {
            IBasketballServices service = new BasketballServicesRpcProxy(host, port);
            logger.debug("BasketballServicesRpcProxy initialized");

            // Set primary stage globally
            SceneManager.setPrimaryStage(primaryStage);
            logger.debug("Primary stage set in SceneManager");

            // Show LoginWindow scene
            logger.debug("Loading LoginWindow.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginWindow.fxml"));
            Scene scene = new Scene(loader.load());

            LoginController controller = loader.getController();
            controller.setService(service);
            logger.debug("Service set in LoginController");

            primaryStage.setTitle("🏀 Basketball Ticket Login");
            primaryStage.setScene(scene);
            primaryStage.show();
            logger.info("Application started and login window displayed");
        } catch (Exception e) {
            logger.error("Error starting client application", e);
            throw e;
        }
    }

    public static void main(String[] args) {
        logger.info("Client application main method invoked");
        launch(args);
    }
}