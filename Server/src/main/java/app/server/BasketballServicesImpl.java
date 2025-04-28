package app.server;

import app.model.Match;
import app.model.Ticket;
import app.model.User;
import app.repository.IMatchRepository;
import app.repository.ITicketRepository;
import app.repository.IUserRepository;
import app.services.IBasketballObserver;
import app.services.IBasketballServices;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BasketballServicesImpl implements IBasketballServices {
    private final IMatchRepository matchRepo;
    private final ITicketRepository ticketRepo;
    private final IUserRepository userRepo;
    private final Map<Integer, IBasketballObserver> loggedClients;

    private final int defaultThreadsNo = 5;

    public BasketballServicesImpl(IMatchRepository matchRepo, ITicketRepository ticketRepo, IUserRepository userRepo) {
        this.matchRepo = matchRepo;
        this.ticketRepo = ticketRepo;
        this.userRepo = userRepo;
        loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized User login(String username, String password, IBasketballObserver clientObserver) {
        System.out.println("[Service] login called with user: " + username);
        User user = userRepo.findByUsernameAndPassword(username, password);
        if (user != null) {
            if (loggedClients.get(user.getId()) != null)
                return null;
            loggedClients.put(user.getId(), clientObserver);
        }
        return user;
    }

    @Override
    public synchronized void logout(User user) {
        System.out.println("[Service] logout called for user: " + user.getUsername());
        loggedClients.remove(user.getId());
    }

    @Override
    public synchronized Iterable<Match> getAvailableMatches() {
        return matchRepo.findAvailableMatches();
    }

    @Override
    public synchronized void sellTicket(int matchId, int userId, String customerName, int seatsToBuy) {
        System.out.println("[Service] sellTicket called for match: " + matchId + ", seats: " + seatsToBuy);
        ticketRepo.save(new Ticket(0, matchId, userId, customerName, seatsToBuy));
        matchRepo.updateSeats(matchId, seatsToBuy);
        notifyClients(userId);
    }

    private void notifyClients(int excludeUserId) {
        System.out.println("[Service] Notifying clients about sold ticket...");
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for (Map.Entry<Integer, IBasketballObserver> entry : loggedClients.entrySet()) {
            int clientId = entry.getKey();
            IBasketballObserver observer = entry.getValue();

            executor.execute(() -> {
                try {
                    observer.ticketSoldUpdate();
                } catch (Exception e) {
                    System.err.println("Error notifying client " + clientId + ": " + e.getMessage());
                }
            });
        }
        executor.shutdown();
    }
}
