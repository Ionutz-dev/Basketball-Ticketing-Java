package app.grpcclient.gui;

import javafx.scene.control.Alert;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
    private static final Logger logger = LogManager.getLogger(Util.class);

    public static void showError(Window owner, String title, String message) {
        logger.debug("Showing error dialog: title='{}', message='{}'", title, message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        logger.debug("Error dialog closed");
    }

    public static void showInfo(Window owner, String title, String message) {
        logger.debug("Showing info dialog: title='{}', message='{}'", title, message);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        logger.debug("Info dialog closed");
    }

    public static void showWarning(Window owner, String title, String message) {
        logger.debug("Showing warning dialog: title='{}', message='{}'", title, message);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        logger.debug("Warning dialog closed");
    }
}