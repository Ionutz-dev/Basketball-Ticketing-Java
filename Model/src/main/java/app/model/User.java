package app.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class User {
    private static final Logger logger = LogManager.getLogger(User.class);

    private int id;
    private String username;
    private String password;

    public User() {
        logger.debug("Creating empty User instance");
    }

    public User(int id, String username, String password) {
        logger.debug("Creating User instance: id={}, username={}", id, username);
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        logger.trace("Setting User id: {}", id);
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    void setUsername(String username) {
        logger.trace("Setting User username: {}", username);
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    void setPassword(String password) {
        logger.trace("Setting User password: [MASKED]");
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + "[MASKED]" + '\'' +
                '}';
    }
}