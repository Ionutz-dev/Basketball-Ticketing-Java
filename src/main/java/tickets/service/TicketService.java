package tickets.service;

import tickets.model.Match;
import tickets.model.Ticket;
import tickets.repository.IMatchRepository;
import tickets.repository.ITicketRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TicketService {
    private final IMatchRepository matchRepo;
    private final ITicketRepository ticketRepo;
    private static final Logger logger = LogManager.getLogger(TicketService.class);

    public TicketService(IMatchRepository matchRepo, ITicketRepository ticketRepo) {
        this.matchRepo = matchRepo;
        this.ticketRepo = ticketRepo;
    }

    // Buy a ticket for a match to a customer, handled by a specific user (userId)
    public void buyTicket(int matchId, int userId, String customerName, int seatsToBuy) {
        Match match = matchRepo.findMatchById(matchId);
        if (match == null) {
            logger.warn("Match not found with ID: " + matchId);
            return;
        }

        if (match.getAvailableSeats() < seatsToBuy) {
            logger.warn("Not enough seats available for match ID: " + matchId);
            return;
        }

        // Save ticket with userId
        Ticket ticket = new Ticket(0, matchId, userId, customerName, seatsToBuy);
        ticketRepo.save(ticket);

        // Update available seats
        matchRepo.updateSeats(matchId, seatsToBuy);

        // Log updated seat info
        Match updatedMatch = matchRepo.findMatchById(matchId);
        if (updatedMatch.getAvailableSeats() == 0) {
            logger.info("Match ID " + matchId + " is SOLD OUT!");
        } else {
            logger.info("Purchased completed. Remaining seats for match ID " + matchId + ": " + updatedMatch.getAvailableSeats());
        }
    }

    // Expose available matches for display (sorted by seats descending)
    public Iterable<Match> getAvailableMatches() {
        return matchRepo.findAvailableMatches();
    }

    // Optional: expose repository if needed by controller
    public IMatchRepository getMatchRepository() {
        return matchRepo;
    }
}
