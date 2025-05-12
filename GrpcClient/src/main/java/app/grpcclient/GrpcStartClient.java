package app.grpcclient;

import app.grpcclient.gui.GrpcLoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GrpcStartClient extends Application {
    private static final Logger logger = LogManager.getLogger(GrpcStartClient.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Starting gRPC Client application");

        try {
            logger.debug("Loading GrpcLoginWindow.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GrpcLoginWindow.fxml"));
            Scene scene = new Scene(loader.load());

            GrpcLoginController controller = loader.getController();
            controller.setStage(primaryStage);
            logger.debug("Stage set in GrpcLoginController");

            primaryStage.setTitle("gRPC Basketball Client");
            primaryStage.setScene(scene);
            primaryStage.show();
            logger.info("Application started and login window displayed");
        } catch (Exception e) {
            logger.error("Error starting gRPC client application", e);
            throw e;
        }
    }

    public static void main(String[] args) {
        logger.info("gRPC Client application main method invoked");
        launch(args);
    }
}