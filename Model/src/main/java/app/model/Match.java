package app.model;

import java.io.Serializable;

public class Match implements Serializable {
    private int id;
    private String teamA;
    private String teamB;
    private double ticketPrice;
    private int availableSeats;

    public Match(int id, String teamA, String teamB, double ticketPrice, int availableSeats) {
        this.id = id;
        this.teamA = teamA;
        this.teamB = teamB;
        this.ticketPrice = ticketPrice;
        this.availableSeats = availableSeats;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTeamA() {
        return teamA;
    }
    public void setTeamA(String teamA) {
        this.teamA = teamA;
    }

    public String getTeamB() {
        return teamB;
    }
    public void setTeamB(String teamB) {
        this.teamB = teamB;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }
    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }
    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", teamA='" + teamA + '\'' +
                ", teamB='" + teamB + '\'' +
                ", ticketPrice=" + ticketPrice +
                ", availableSeats=" + availableSeats +
                '}';
    }
}
