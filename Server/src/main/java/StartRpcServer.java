import app.network.utils.BasketballRpcConcurrentServer;
import app.repository.IMatchRepository;
import app.repository.ITicketRepository;
import app.repository.IUserRepository;
import app.repository.jdbc.MatchRepositoryJdbc;
import app.repository.jdbc.TicketRepositoryJdbc;
import app.repository.jdbc.UserRepositoryJdbc;
import app.server.BasketballServicesImpl;
import app.services.IBasketballServices;
import app.network.utils.ServerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class StartRpcServer {
    private static final Logger logger = LogManager.getLogger(StartRpcServer.class);
    private static final int DEFAULT_PORT = 55555;

    public static void main(String[] args) {
        logger.info("Starting RPC Server with JDBC repositories");

        Properties serverProps = new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/appserver.properties"));
            logger.info("Server properties loaded");
            serverProps.list(System.out);
        } catch (IOException e) {
            logger.error("Cannot load server config", e);
            return;
        }

        logger.debug("Initializing JDBC repositories");
        IMatchRepository matchRepo = new MatchRepositoryJdbc(serverProps);
        ITicketRepository ticketRepo = new TicketRepositoryJdbc(serverProps);
        IUserRepository userRepo = new UserRepositoryJdbc(serverProps);

        logger.debug("Creating BasketballServicesImpl instance");
        IBasketballServices service = new BasketballServicesImpl(matchRepo, ticketRepo, userRepo);

        int port = DEFAULT_PORT;
        try {
            port = Integer.parseInt(serverProps.getProperty("app.server.port"));
            logger.info("Using port from properties: {}", port);
        } catch (NumberFormatException ignored) {
            logger.warn("Invalid port in config. Using default: {}", DEFAULT_PORT);
        }

        logger.info("Starting RPC Server on port {}", port);
        BasketballRpcConcurrentServer server = new BasketballRpcConcurrentServer(port, service);
        try {
            server.start();
        } catch (ServerException e) {
            logger.error("Server error", e);
        }
    }
}