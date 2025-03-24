package tickets.repository;

import tickets.model.Match;
import java.util.List;

public interface IMatchRepository {
    List<Match> findAll();
    List<Match> findAvailableMatches();
    void updateSeats(int matchId, int seatsSold);
    Match findMatchById(int matchId);
}
