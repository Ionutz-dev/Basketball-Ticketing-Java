package app.server;

import app.network.utils.BasketballRpcConcurrentServer;
import app.repository.IMatchRepository;
import app.repository.ITicketRepository;
import app.repository.IUserRepository;
import app.repository.jdbc.MatchRepositoryJdbc;
import app.repository.jdbc.TicketRepositoryJdbc;
import app.repository.jdbc.UserRepositoryJdbc;
import app.services.BasketballServiceImpl;
import app.services.IBasketballService;
import app.network.utils.ServerException;

import java.io.IOException;
import java.util.Properties;

public class StartRpcServer {
    private static final int DEFAULT_PORT = 55555;

    public static void main(String[] args) {
        Properties serverProps = new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/appserver.properties"));
            System.out.println("✅ Server properties loaded:");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("❌ Cannot load server config: " + e.getMessage());
            return;
        }

        // Wiring services
        IMatchRepository matchRepo = new MatchRepositoryJdbc(serverProps);
        ITicketRepository ticketRepo = new TicketRepositoryJdbc(serverProps);
        IUserRepository userRepo = new UserRepositoryJdbc(serverProps);
        IBasketballService service = new BasketballServiceImpl(matchRepo, ticketRepo, userRepo);

        int port = DEFAULT_PORT;
        try {
            port = Integer.parseInt(serverProps.getProperty("app.server.port"));
        } catch (NumberFormatException ignored) {
            System.out.println("⚠️ Invalid port in config. Using default: " + DEFAULT_PORT);
        }

        BasketballRpcConcurrentServer server = new BasketballRpcConcurrentServer(port, service);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("❌ Server error: " + e.getMessage());
        }
    }
}
