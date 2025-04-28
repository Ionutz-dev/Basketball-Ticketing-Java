package app.network.dto;

import java.io.Serializable;

public class TicketDTO implements Serializable {
    private int id;
    private int matchId;
    private int userId;
    private String customerName;
    private int seatsSold;

    public TicketDTO(int id, int matchId, int userId, String customerName, int seatsSold) {
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
        return "TicketDTO{" +
                "id=" + id +
                ", matchId=" + matchId +
                ", userId=" + userId +
                ", customerName='" + customerName + '\'' +
                ", seatsSold=" + seatsSold +
                '}';
    }
}
