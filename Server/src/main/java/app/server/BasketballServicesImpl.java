package app.server;

import app.model.Match;
import app.model.Ticket;
import app.model.User;
import app.repository.IMatchRepository;
import app.repository.ITicketRepository;
import app.repository.IUserRepository;
import app.services.BasketballException;
import app.services.IBasketballObserver;
import app.services.IBasketballServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BasketballServicesImpl implements IBasketballServices {
    private static final Logger logger = LogManager.getLogger(BasketballServicesImpl.class);

    private final IMatchRepository matchRepo;
    private final ITicketRepository ticketRepo;
    private final IUserRepository userRepo;
    private final Map<Integer, IBasketballObserver> loggedClients;

    private final int defaultThreadsNo = 5;

    public BasketballServicesImpl(IMatchRepository matchRepo, ITicketRepository ticketRepo, IUserRepository userRepo) {
        logger.info("Initializing BasketballServicesImpl");
        logger.debug("Repository types - Match: {}, Ticket: {}, User: {}",
                matchRepo.getClass().getSimpleName(),
                ticketRepo.getClass().getSimpleName(),
                userRepo.getClass().getSimpleName());

        this.matchRepo = matchRepo;
        this.ticketRepo = ticketRepo;
        this.userRepo = userRepo;
        loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized User login(String username, String password, IBasketballObserver clientObserver)
            throws BasketballException {
        logger.info("Login request: username={}", username);
        try {
            User user = userRepo.findByUsernameAndPassword(username, password);
            if (user != null) {
                if (loggedClients.get(user.getId()) != null) {
                    logger.warn("User already logged in: username={}, id={}", username, user.getId());
                    return null;
                }
                loggedClients.put(user.getId(), clientObserver);
                logger.info("Login successful: username={}, id={}, total logged clients: {}",
                        username, user.getId(), loggedClients.size());
                return user;
            } else {
                logger.warn("Login failed: Invalid credentials for username={}", username);
                return null;
            }
        } catch (Exception e) {
            logger.error("Login error for username={}", username, e);
            throw new BasketballException("Login failed: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized void logout(User user) throws BasketballException {
        if (user == null) {
            logger.warn("Logout called with null user");
            return;
        }

        logger.info("Logout: username={}, id={}", user.getUsername(), user.getId());
        try {
            IBasketballObserver removed = loggedClients.remove(user.getId());
            if (removed != null) {
                logger.info("User successfully logged out: username={}, id={}, remaining clients: {}",
                        user.getUsername(), user.getId(), loggedClients.size());
            } else {
                logger.warn("User not found in logged clients: username={}, id={}",
                        user.getUsername(), user.getId());
            }
        } catch (Exception e) {
            logger.error("Logout error for user: {}", user, e);
            throw new BasketballException("Logout failed: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized Iterable<Match> getAvailableMatches() throws BasketballException {
        logger.info("Getting available matches");
        try {
            Iterable<Match> matches = matchRepo.findAvailableMatches();
            logger.debug("Retrieved available matches");
            return matches;
        } catch (Exception e) {
            logger.error("Error retrieving available matches", e);
            throw new BasketballException("Error retrieving available matches: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized void sellTicket(int matchId, int userId, String customerName, int seatsToBuy)
            throws BasketballException {
        logger.info("Selling ticket: matchId={}, userId={}, customer={}, seats={}",
                matchId, userId, customerName, seatsToBuy);

        try {
            // First, check if the match exists and has enough seats
            Match match = matchRepo.findMatchById(matchId);
            if (match == null) {
                logger.warn("Cannot sell ticket - match not found: matchId={}", matchId);
                throw new BasketballException("Match not found with ID: " + matchId);
            }

            if (match.getAvailableSeats() < seatsToBuy) {
                logger.warn("Cannot sell ticket - not enough seats: matchId={}, requested={}, available={}",
                        matchId, seatsToBuy, match.getAvailableSeats());
                throw new BasketballException("Not enough seats available");
            }

            // Save the ticket
            logger.debug("Creating ticket record");
            ticketRepo.save(new Ticket(0, matchId, userId, customerName, seatsToBuy));

            // Update the seats
            logger.debug("Updating available seats");
            matchRepo.updateSeats(matchId, seatsToBuy);

            // Notify clients
            logger.debug("Notifying other clients about the ticket sale");
            notifyClients(userId);

            logger.info("Ticket sold successfully: matchId={}, seats={}", matchId, seatsToBuy);
        } catch (BasketballException e) {
            logger.error("Error selling ticket", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error selling ticket", e);
            throw new BasketballException("Error selling ticket: " + e.getMessage(), e);
        }
    }

    private void notifyClients(int excludeUserId) {
        logger.debug("Notifying clients about sold ticket, excluding userId={}", excludeUserId);
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);

        int totalClients = loggedClients.size();
        logger.debug("Number of logged clients to notify: {}", totalClients);

        for (Map.Entry<Integer, IBasketballObserver> entry : loggedClients.entrySet()) {
            int clientId = entry.getKey();
            IBasketballObserver observer = entry.getValue();

            executor.execute(() -> {
                try {
                    logger.debug("Notifying client: id={}", clientId);
                    observer.ticketSoldUpdate();
                    logger.debug("Successfully notified client: id={}", clientId);
                } catch (BasketballException e) {
                    logger.error("Error notifying client: id={}", clientId, e);
                } catch (Exception e) {
                    logger.error("Unexpected error notifying client: id={}", clientId, e);
                }
            });
        }

        executor.shutdown();
        logger.debug("Notification tasks submitted, executor shutting down");
    }
}