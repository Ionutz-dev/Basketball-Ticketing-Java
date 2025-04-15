module tickets {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;

    opens tickets to javafx.fxml;
    opens tickets.controller to javafx.fxml;

    exports tickets;
    exports tickets.controller;
    exports tickets.network;
}
