package app.model.hibernate;

import app.model.Ticket;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@Table(name = "Tickets")
public class HibernateTicket {
    private static final Logger logger = LogManager.getLogger(HibernateTicket.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int matchId;
    private int userId;
    private String customerName;
    private int seatsSold;

    public HibernateTicket() {
        logger.debug("Creating empty HibernateTicket instance");
    }

    public HibernateTicket(Ticket ticket) {
        logger.debug("Creating HibernateTicket from Ticket: id={}, matchId={}, userId={}, seats={}",
                ticket.getId(), ticket.getMatchId(), ticket.getUserId(), ticket.getSeatsSold());

        this.id = ticket.getId();
        this.matchId = ticket.getMatchId();
        this.userId = ticket.getUserId();
        this.customerName = ticket.getCustomerName();
        this.seatsSold = ticket.getSeatsSold();
    }

    public Ticket toTicket() {
        logger.debug("Converting HibernateTicket to Ticket: id={}", id);
        return new Ticket(id, matchId, userId, customerName, seatsSold);
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        logger.trace("Setting HibernateTicket id: {}", id);
        this.id = id;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        logger.trace("Setting HibernateTicket matchId: {}", matchId);
        this.matchId = matchId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        logger.trace("Setting HibernateTicket userId: {}", userId);
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        logger.trace("Setting HibernateTicket customerName: {}", customerName);
        this.customerName = customerName;
    }

    public int getSeatsSold() {
        return seatsSold;
    }

    public void setSeatsSold(int seatsSold) {
        logger.trace("Setting HibernateTicket seatsSold: {}", seatsSold);
        this.seatsSold = seatsSold;
    }

    @Override
    public String toString() {
        return "HibernateTicket{" +
                "id=" + id +
                ", matchId=" + matchId +
                ", userId=" + userId +
                ", customerName='" + customerName + '\'' +
                ", seatsSold=" + seatsSold +
                '}';
    }
}