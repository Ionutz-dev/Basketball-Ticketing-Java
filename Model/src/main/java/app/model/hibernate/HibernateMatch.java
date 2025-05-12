package app.model.hibernate;

import app.model.Match;
import jakarta.persistence.*;

@Entity
@Table(name = "Matches")
public class HibernateMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String teamA;
    private String teamB;
    private double ticketPrice;
    private int availableSeats;

    public HibernateMatch() {
        // Required by Hibernate
    }

    public HibernateMatch(Match match) {
        this.id = match.getId();
        this.teamA = match.getTeamA();
        this.teamB = match.getTeamB();
        this.ticketPrice = match.getTicketPrice();
        this.availableSeats = match.getAvailableSeats();
    }

    public Match toMatch() {
        return new Match(id, teamA, teamB, ticketPrice, availableSeats);
    }

    // Getters and setters
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
        return "HibernateMatch{" +
                "id=" + id +
                ", teamA='" + teamA + '\'' +
                ", teamB='" + teamB + '\'' +
                ", ticketPrice=" + ticketPrice +
                ", availableSeats=" + availableSeats +
                '}';
    }
}