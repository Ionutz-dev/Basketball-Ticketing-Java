package app.client;

import app.client.gui.LoginController;
import app.network.rpcprotocol.BasketballServicesRpcProxy;
import app.services.IBasketballService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartRpcClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        String host = "localhost";
        int port = 55556;

        IBasketballService service = new BasketballServicesRpcProxy(host, port);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
        Scene scene = new Scene(loader.load());

        LoginController controller = loader.getController();
        controller.setService(service);
        controller.setPrimaryStage(primaryStage); // 🔑 so it can switch scene later

        primaryStage.setTitle("🏀 Basketball Ticket Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
