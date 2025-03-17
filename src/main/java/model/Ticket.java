package model;

import java.time.LocalDateTime;

public class Ticket {
    private int id;
    private int matchId;
    private String customerName;
    private int seatsSold;

    public Ticket(int id, int matchId, String customerName, int seatsSold) {
        this.id = id;
        this.matchId = matchId;
        this.customerName = customerName;
        this.seatsSold = seatsSold;
    }

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
        return "Ticket{" +
                "id=" + id +
                ", matchId=" + matchId +
                ", customerName='" + customerName + '\'' +
                ", seatsSold=" + seatsSold +
                '}';
    }
}
