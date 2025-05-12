package app.repository.hibernate;

import app.model.hibernate.HibernateMatch;
import app.model.hibernate.HibernateTicket;
import app.model.hibernate.HibernateUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public class HibernateUtils {
    private static final Logger logger = LogManager.getLogger(HibernateUtils.class);
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory(Properties props) {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            logger.debug("Creating new Hibernate SessionFactory");
            sessionFactory = createSessionFactory(props);
        } else {
            logger.trace("Reusing existing Hibernate SessionFactory");
        }
        return sessionFactory;
    }

    private static SessionFactory createSessionFactory(Properties props) {
        logger.info("Initializing Hibernate SessionFactory");
        try {
            Configuration configuration = new Configuration();

            // Database connection settings
            Properties hibernateProps = new Properties();
            String driver = props.getProperty("jdbc.driver");
            String url = props.getProperty("jdbc.url");
            String user = props.getProperty("jdbc.user", "");
            String pass = props.getProperty("jdbc.pass", "");

            logger.debug("Configuring Hibernate connection: driver={}, url={}", driver, url);

            hibernateProps.put("hibernate.connection.driver_class", driver);
            hibernateProps.put("hibernate.connection.url", url);
            hibernateProps.put("hibernate.connection.username", user);
            hibernateProps.put("hibernate.connection.password", pass);

            // SQLite dialect
            hibernateProps.put("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");

            // Other Hibernate properties
            hibernateProps.put("hibernate.show_sql", "true");
            hibernateProps.put("hibernate.format_sql", "true");
            hibernateProps.put("hibernate.hbm2ddl.auto", "update");

            configuration.setProperties(hibernateProps);

            // Add annotated classes
            logger.debug("Registering entity classes with Hibernate");
            configuration.addAnnotatedClass(HibernateMatch.class);
            configuration.addAnnotatedClass(HibernateTicket.class);
            configuration.addAnnotatedClass(HibernateUser.class);

            logger.info("Building Hibernate SessionFactory");
            return configuration.buildSessionFactory();
        } catch (Exception e) {
            logger.error("Error creating Hibernate SessionFactory", e);
            throw new RuntimeException("Error creating Hibernate SessionFactory", e);
        }
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            logger.info("Closing Hibernate SessionFactory");
            sessionFactory.close();
            logger.debug("Hibernate SessionFactory closed successfully");
        } else {
            logger.debug("No open SessionFactory to close");
        }
    }
}