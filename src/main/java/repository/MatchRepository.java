package repository;

import model.Match;
import java.util.List;

public interface MatchRepository {
    List<Match> findAllAvailableMatches();
    Match findMatchById(int matchID);
    void updateAvailableSeats(int matchID, int seatsSold);
}
