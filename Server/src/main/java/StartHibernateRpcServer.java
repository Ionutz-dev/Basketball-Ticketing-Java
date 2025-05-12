import app.network.utils.BasketballRpcConcurrentServer;
import app.network.utils.ServerException;
import app.repository.IMatchRepository;
import app.repository.ITicketRepository;
import app.repository.IUserRepository;
import app.repository.hibernate.MatchRepositoryHibernate;
import app.repository.hibernate.TicketRepositoryHibernate;
import app.repository.hibernate.UserRepositoryHibernate;
import app.server.BasketballServicesImpl;
import app.services.IBasketballServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class StartHibernateRpcServer {
    private static final Logger logger = LogManager.getLogger(StartHibernateRpcServer.class);
    private static final int DEFAULT_PORT = 55556;

    public static void main(String[] args) {
        logger.info("Starting Hibernate RPC Server");

        Properties serverProps = new Properties();
        try {
            serverProps.load(StartHibernateRpcServer.class.getResourceAsStream("/appserver.properties"));
            logger.info("Server properties loaded");
            serverProps.list(System.out);
        } catch (IOException e) {
            logger.error("Cannot load server config", e);
            return;
        }

        logger.debug("Initializing Hibernate repositories");
        IMatchRepository matchRepo = new MatchRepositoryHibernate(serverProps);
        ITicketRepository ticketRepo = new TicketRepositoryHibernate(serverProps);
        IUserRepository userRepo = new UserRepositoryHibernate(serverProps);

        logger.debug("Creating BasketballServicesImpl instance");
        IBasketballServices service = new BasketballServicesImpl(matchRepo, ticketRepo, userRepo);

        int port = DEFAULT_PORT;
        try {
            port = Integer.parseInt(serverProps.getProperty("app.server.port"));
            logger.info("Using port from properties: {}", port);
        } catch (NumberFormatException ignored) {
            logger.warn("Invalid port in config. Using default: {}", DEFAULT_PORT);
        }

        logger.info("Starting RPC Server with Hibernate persistence on port {}", port);
        BasketballRpcConcurrentServer server = new BasketballRpcConcurrentServer(port, service);
        try {
            server.start();
        } catch (ServerException e) {
            logger.error("Server failed to start", e);
        }
    }
}