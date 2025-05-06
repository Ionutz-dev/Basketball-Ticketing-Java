package app.grpcclient;

import app.grpcclient.gui.GrpcLoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GrpcStartClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GrpcLoginWindow.fxml"));
        Scene scene = new Scene(loader.load());

        GrpcLoginController controller = loader.getController();
        controller.setStage(primaryStage); // ✅ Pass stage for reuse

        primaryStage.setTitle("gRPC Basketball Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
