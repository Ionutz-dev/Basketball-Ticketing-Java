package repository;

import model.Match;
import java.util.List;

public interface IMatchRepository {
    List<Match> findAll();
    List<Match> findAvailableMatches();
    void updateSeats(int matchId, int seatsSold);
}
