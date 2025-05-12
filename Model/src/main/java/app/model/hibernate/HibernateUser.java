package app.model.hibernate;

import app.model.User;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@Table(name = "Users")
public class HibernateUser {
    private static final Logger logger = LogManager.getLogger(HibernateUser.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String password;

    public HibernateUser() {
        logger.debug("Creating empty HibernateUser instance");
    }

    public HibernateUser(User user) {
        logger.debug("Creating HibernateUser from User: id={}, username={}",
                user.getId(), user.getUsername());

        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
    }

    public User toUser() {
        logger.debug("Converting HibernateUser to User: id={}", id);
        return new User(id, username, password);
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        logger.trace("Setting HibernateUser id: {}", id);
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        logger.trace("Setting HibernateUser username: {}", username);
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        logger.trace("Setting HibernateUser password: [MASKED]");
        this.password = password;
    }

    @Override
    public String toString() {
        return "HibernateUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + "[MASKED]" + '\'' +
                '}';
    }
}