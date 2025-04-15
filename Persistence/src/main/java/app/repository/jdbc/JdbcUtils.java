package app.repository.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcUtils {
    private final Properties props;

    public JdbcUtils(Properties props) {
        this.props = props;
    }

    private static Connection instance = null;

    private Connection getNewConnection() {
        System.out.println("Connecting to: " + props.getProperty("jdbc.url"));

        try {
            String driver = props.getProperty("jdbc.driver");
            String url = props.getProperty("jdbc.url");
            String user = props.getProperty("jdbc.user");
            String pass = props.getProperty("jdbc.pass");

            Class.forName(driver);
            return DriverManager.getConnection(url, user, pass);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Connection getConnection() {
        try {
            if (instance == null || instance.isClosed())
                instance = getNewConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instance;
    }
}
