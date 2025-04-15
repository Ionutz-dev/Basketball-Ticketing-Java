package app.services;

import app.model.Match;
import app.model.Ticket;
import app.model.User;
import app.repository.IMatchRepository;
import app.repository.ITicketRepository;
import app.repository.IUserRepository;
import app.network.rpcprotocol.BasketballClientRpcWorker; // ✅ this fixes "cannot resolve symbol"

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BasketballServiceImpl implements IBasketballService {
    private final IMatchRepository matchRepo;
    private final ITicketRepository ticketRepo;
    private final IUserRepository userRepo;
    private final Map<Integer, IBasketballObserver> loggedClients;

    public BasketballServiceImpl(IMatchRepository matchRepo, ITicketRepository ticketRepo, IUserRepository userRepo) {
        this.matchRepo = matchRepo;
        this.ticketRepo = ticketRepo;
        this.userRepo = userRepo;
        loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public User login(String username, String password, IBasketballObserver clientObserver) {
        User user = userRepo.findByUsernameAndPassword(username, password);
        if (user != null) {
            loggedClients.put(user.getId(), clientObserver);
        }
        return user;
    }

    @Override
    public void logout(User user) {
        loggedClients.remove(user.getId());
    }

    @Override
    public Iterable<Match> getAvailableMatches() {
        return matchRepo.findAvailableMatches();
    }

    @Override
    public void buyTicket(int matchId, int userId, String customerName, int seatsToBuy) {
        ticketRepo.save(new Ticket(0, matchId, userId, customerName, seatsToBuy));
        matchRepo.updateSeats(matchId, seatsToBuy);
        notifyClients(userId); // ✅ only push to others
    }

    private void notifyClients(int excludeUserId) {
        for (Map.Entry<Integer, IBasketballObserver> entry : loggedClients.entrySet()) {
            int clientId = entry.getKey();
            IBasketballObserver observer = entry.getValue();

            if (clientId == excludeUserId) continue; // ✅ Skip push to requester

            try {
                if (observer instanceof BasketballClientRpcWorker worker) {
                    worker.sendPushUpdate(); // send TICKET_BOUGHT
                }
            } catch (Exception e) {
                System.err.println("Failed to notify client " + clientId + ": " + e.getMessage());
            }
        }
    }
}
