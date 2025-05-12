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
            sessionFactory = createSessionFactory(props);
        }
        return sessionFactory;
    }

    private static SessionFactory createSessionFactory(Properties props) {
        logger.info("Creating Hibernate SessionFactory");
        try {
            Configuration configuration = new Configuration();

            // Database connection settings
            Properties hibernateProps = new Properties();
            hibernateProps.put("hibernate.connection.driver_class", props.getProperty("jdbc.driver"));
            hibernateProps.put("hibernate.connection.url", props.getProperty("jdbc.url"));
            hibernateProps.put("hibernate.connection.username", props.getProperty("jdbc.user", ""));
            hibernateProps.put("hibernate.connection.password", props.getProperty("jdbc.pass", ""));

            // SQLite dialect
            hibernateProps.put("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");

            // Other Hibernate properties
            hibernateProps.put("hibernate.show_sql", "true");
            hibernateProps.put("hibernate.format_sql", "true");
            hibernateProps.put("hibernate.hbm2ddl.auto", "update");

            configuration.setProperties(hibernateProps);

            // Add annotated classes
            configuration.addAnnotatedClass(HibernateMatch.class);
            configuration.addAnnotatedClass(HibernateTicket.class);
            configuration.addAnnotatedClass(HibernateUser.class);

            logger.info("Hibernate configuration created, building SessionFactory");
            return configuration.buildSessionFactory();
        } catch (Exception e) {
            logger.error("Error creating SessionFactory", e);
            throw new RuntimeException("Error creating SessionFactory", e);
        }
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            logger.info("Closing Hibernate SessionFactory");
            sessionFactory.close();
        }
    }
}