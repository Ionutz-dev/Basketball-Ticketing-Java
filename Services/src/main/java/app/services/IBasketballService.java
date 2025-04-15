package app.services;

import app.model.Match;
import app.model.User;

public interface IBasketballService {
    User login(String username, String password, IBasketballObserver clientObserver);
    void logout(User user);
    Iterable<Match> getAvailableMatches();
    void buyTicket(int matchId, int userId, String customerName, int seatsToBuy);
}
