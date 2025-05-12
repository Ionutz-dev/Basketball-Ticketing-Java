package app.repository.hibernate;

import app.model.User;
import app.model.hibernate.HibernateUser;
import app.repository.IUserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.Properties;

public class UserRepositoryHibernate implements IUserRepository {
    private static final Logger logger = LogManager.getLogger(UserRepositoryHibernate.class);
    private final SessionFactory sessionFactory;

    public UserRepositoryHibernate(Properties props) {
        this.sessionFactory = HibernateUtils.getSessionFactory(props);
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        logger.info("Finding user with username={}", username);
        User user = null;

        try (Session session = sessionFactory.openSession()) {
            Query<HibernateUser> query = session.createQuery(
                    "FROM HibernateUser WHERE username = :username AND password = :password",
                    HibernateUser.class
            );
            query.setParameter("username", username);
            query.setParameter("password", password);

            HibernateUser hibernateUser = query.uniqueResult();

            if (hibernateUser != null) {
                user = hibernateUser.toUser();
                logger.info("Found user: {}", user.getUsername());
            } else {
                logger.warn("User with username {} not found", username);
            }
        } catch (Exception e) {
            logger.error("Error finding user by username and password", e);
        }

        return user;
    }
}