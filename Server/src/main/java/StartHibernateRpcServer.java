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

import java.io.IOException;
import java.util.Properties;

public class StartHibernateRpcServer {
    private static final int DEFAULT_PORT = 55556;

    public static void main(String[] args) {
        Properties serverProps = new Properties();
        try {
            serverProps.load(StartHibernateRpcServer.class.getResourceAsStream("/appserver.properties"));
            System.out.println("Server properties loaded for Hibernate server:");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot load server config: " + e.getMessage());
            return;
        }

        // Use Hibernate repositories
        IMatchRepository matchRepo = new MatchRepositoryHibernate(serverProps);
        ITicketRepository ticketRepo = new TicketRepositoryHibernate(serverProps);
        IUserRepository userRepo = new UserRepositoryHibernate(serverProps);
        IBasketballServices service = new BasketballServicesImpl(matchRepo, ticketRepo, userRepo);

        int port = DEFAULT_PORT;
        try {
            port = Integer.parseInt(serverProps.getProperty("app.server.port"));
        } catch (NumberFormatException ignored) {
            System.out.println("Invalid port in config. Using default: " + DEFAULT_PORT);
        }

        System.out.println("Starting RPC Server with Hibernate persistence on port " + port);
        BasketballRpcConcurrentServer server = new BasketballRpcConcurrentServer(port, service);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}