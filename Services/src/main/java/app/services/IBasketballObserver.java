package app.services;

import java.io.Serializable;

public interface IBasketballObserver extends Serializable {
    void ticketSoldUpdate() throws BasketballException;
}
