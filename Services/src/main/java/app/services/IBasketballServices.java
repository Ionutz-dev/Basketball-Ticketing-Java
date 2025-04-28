package app.services;

import app.model.Match;
import app.model.User;

public interface IBasketballServices {
    User login(String username, String password, IBasketballObserver clientObserver) throws BasketballException;
    void logout(User user) throws BasketballException;
    Iterable<Match> getAvailableMatches() throws BasketballException;
    void sellTicket(int matchId, int userId, String customerName, int seatsToBuy) throws BasketballException;
}
