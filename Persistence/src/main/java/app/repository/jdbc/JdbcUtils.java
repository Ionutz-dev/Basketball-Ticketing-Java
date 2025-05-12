package app.repository.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcUtils {
    private static final Logger logger = LogManager.getLogger(JdbcUtils.class);
    private final Properties props;

    public JdbcUtils(Properties props) {
        logger.debug("Initializing JdbcUtils with properties");
        this.props = props;
    }

    private static Connection instance = null;

    private Connection getNewConnection() {
        String url = props.getProperty("jdbc.url");
        logger.info("Creating new database connection to: {}", url);

        try {
            String driver = props.getProperty("jdbc.driver");
            String user = props.getProperty("jdbc.user");
            String pass = props.getProperty("jdbc.pass");

            logger.debug("Using JDBC driver: {}", driver);
            Class.forName(driver);

            Connection connection = DriverManager.getConnection(url, user, pass);
            logger.info("Successfully established database connection");
            return connection;

        } catch (ClassNotFoundException e) {
            logger.error("JDBC driver not found", e);
            return null;
        } catch (SQLException e) {
            logger.error("Error connecting to database", e);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error creating database connection", e);
            return null;
        }
    }

    public Connection getConnection() {
        try {
            if (instance == null || instance.isClosed()) {
                logger.debug("Getting new database connection - current connection is null or closed");
                instance = getNewConnection();
            } else {
                logger.trace("Reusing existing database connection");
            }
        } catch (SQLException e) {
            logger.error("Error checking connection state", e);
        }
        return instance;
    }
}