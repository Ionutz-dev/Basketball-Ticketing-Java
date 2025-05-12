package app.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ticket {
    private static final Logger logger = LogManager.getLogger(Ticket.class);

    private int id;
    private int matchId;
    private int userId;
    private String customerName;
    private int seatsSold;

    public Ticket() {
        logger.debug("Creating empty Ticket instance");
    }

    public Ticket(int id, int matchId, int userId, String customerName, int seatsSold) {
        logger.debug("Creating Ticket instance: id={}, matchId={}, userId={}, customer={}, seats={}",
                id, matchId, userId, customerName, seatsSold);
        this.id = id;
        this.matchId = matchId;
        this.userId = userId;
        this.customerName = customerName;
        this.seatsSold = seatsSold;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        logger.trace("Setting Ticket id: {}", id);
        this.id = id;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        logger.trace("Setting Ticket matchId: {}", matchId);
        this.matchId = matchId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        logger.trace("Setting Ticket userId: {}", userId);
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        logger.trace("Setting Ticket customerName: {}", customerName);
        this.customerName = customerName;
    }

    public int getSeatsSold() {
        return seatsSold;
    }

    public void setSeatsSold(int seatsSold) {
        logger.trace("Setting Ticket seatsSold: {}", seatsSold);
        this.seatsSold = seatsSold;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", matchId=" + matchId +
                ", userId=" + userId +
                ", customerName='" + customerName + '\'' +
                ", seatsSold=" + seatsSold +
                '}';
    }
}