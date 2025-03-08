package repository;

import model.Match;
import java.util.List;

public interface IMatchRepository {
    List<Match> findAllAvailableMatches();
    Match findMatchById(int matchID);
    void updateAvailableSeats(int matchID, int seatsSold);
}
