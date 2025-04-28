package app.repository.jdbc;

import app.model.User;
import app.repository.IUserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class UserRepositoryJdbc implements IUserRepository {
    private final JdbcUtils jdbcUtils;

    public UserRepositoryJdbc(Properties props) {
        this.jdbcUtils = new JdbcUtils(props);
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        System.out.println("Looking for user: " + username + ", " + password);

        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
