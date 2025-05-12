package app.repository.jdbc;

import app.model.User;
import app.repository.IUserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class UserRepositoryJdbc implements IUserRepository {
    private static final Logger logger = LogManager.getLogger(UserRepositoryJdbc.class);
    private final JdbcUtils jdbcUtils;

    public UserRepositoryJdbc(Properties props) {
        logger.info("Initializing JDBC User Repository");
        this.jdbcUtils = new JdbcUtils(props);
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        logger.debug("Finding user by username={}", username);
        // Don't log passwords, even in debug mode

        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            logger.debug("Executing SQL: SELECT * FROM Users WHERE username = ? AND password = ?");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
                logger.info("Found user with username={}", username);
                return user;
            } else {
                logger.warn("No user found with username={}", username);
            }
        } catch (SQLException e) {
            logger.error("SQL error finding user by username and password", e);
        } catch (Exception e) {
            logger.error("Unexpected error finding user by username and password", e);
        }
        return null;
    }
}