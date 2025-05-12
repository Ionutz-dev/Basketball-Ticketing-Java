package app.grpcclient.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SceneManager {
    private static final Logger logger = LogManager.getLogger(SceneManager.class);
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        logger.debug("Setting primary stage in GrpcClient SceneManager");
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void changeScene(String fxmlFile, Object controller, String title) {
        logger.info("Changing scene to: {}, title: {}", fxmlFile, title);
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlFile));
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
            logger.debug("Scene change completed");
        } catch (Exception e) {
            logger.error("Error changing scene to: {}", fxmlFile, e);
            e.printStackTrace();
        }
    }
}