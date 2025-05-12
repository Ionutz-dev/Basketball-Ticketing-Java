package app.model.hibernate;

import app.model.Ticket;
import jakarta.persistence.*;

@Entity
@Table(name = "Tickets")
public class HibernateTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int matchId;
    private int userId;
    private String customerName;
    private int seatsSold;

    public HibernateTicket() {
        // Required by Hibernate
    }

    public HibernateTicket(Ticket ticket) {
        this.id = ticket.getId();
        this.matchId = ticket.getMatchId();
        this.userId = ticket.getUserId();
        this.customerName = ticket.getCustomerName();
        this.seatsSold = ticket.getSeatsSold();
    }

    public Ticket toTicket() {
        return new Ticket(id, matchId, userId, customerName, seatsSold);
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getSeatsSold() {
        return seatsSold;
    }

    public void setSeatsSold(int seatsSold) {
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