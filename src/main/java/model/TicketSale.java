package model;

import java.time.LocalDateTime;

public class TicketSale {
    private int saleID;
    private int matchID;
    private String customerName;
    private int numberOfSeats;
    private LocalDateTime saleDateTime;

    public TicketSale(int saleID, int matchID, String customerName, int numberOfSeats, LocalDateTime saleDateTime) {
        this.saleID = saleID;
        this.matchID = matchID;
        this.customerName = customerName;
        this.numberOfSeats = numberOfSeats;
        this.saleDateTime = saleDateTime;
    }

    public int getSaleID() {
        return saleID;
    }

    public void setSaleID(int saleID) {
        this.saleID = saleID;
    }

    public int getMatchID() {
        return matchID;
    }

    public void setMatchID(int matchID) {
        this.matchID = matchID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public LocalDateTime getSaleDateTime() {
        return saleDateTime;
    }

    public void setSaleDateTime(LocalDateTime saleDateTime) {
        this.saleDateTime = saleDateTime;
    }

    @Override
    public String toString() {
        return "TicketSale{" +
                "saleID=" + saleID +
                ", matchID=" + matchID +
                ", customerName='" + customerName + '\'' +
                ", numberOfSeats=" + numberOfSeats +
                ", saleDateTime=" + saleDateTime +
                '}';
    }
}
