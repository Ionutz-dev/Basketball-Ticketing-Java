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
        logger.info("Initializing Hibernate User Repository");
        this.sessionFactory = HibernateUtils.getSessionFactory(props);
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        logger.debug("Finding user with username={} using Hibernate", username);
        User user = null;

        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM HibernateUser WHERE username = :username AND password = :password";
            logger.debug("Executing HQL: {}", hql);

            Query<HibernateUser> query = session.createQuery(hql, HibernateUser.class);
            query.setParameter("username", username);
            query.setParameter("password", password);

            HibernateUser hibernateUser = query.uniqueResult();

            if (hibernateUser != null) {
                user = hibernateUser.toUser();
                logger.info("Found user with username={}, id={}", username, user.getId());
            } else {
                logger.warn("No user found with username={}", username);
            }
        } catch (Exception e) {
            logger.error("Error finding user by username and password with Hibernate", e);
        }

        return user;
    }
}