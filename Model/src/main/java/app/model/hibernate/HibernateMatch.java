package app.model.hibernate;

import app.model.Match;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@Table(name = "Matches")
public class HibernateMatch {
    private static final Logger logger = LogManager.getLogger(HibernateMatch.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String teamA;
    private String teamB;
    private double ticketPrice;
    private int availableSeats;

    public HibernateMatch() {
        logger.debug("Creating empty HibernateMatch instance");
    }

    public HibernateMatch(Match match) {
        logger.debug("Creating HibernateMatch from Match: id={}, teamA={}, teamB={}",
                match.getId(), match.getTeamA(), match.getTeamB());

        this.id = match.getId();
        this.teamA = match.getTeamA();
        this.teamB = match.getTeamB();
        this.ticketPrice = match.getTicketPrice();
        this.availableSeats = match.getAvailableSeats();
    }

    public Match toMatch() {
        logger.debug("Converting HibernateMatch to Match: id={}", id);
        return new Match(id, teamA, teamB, ticketPrice, availableSeats);
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        logger.trace("Setting HibernateMatch id: {}", id);
        this.id = id;
    }

    public String getTeamA() {
        return teamA;
    }

    public void setTeamA(String teamA) {
        logger.trace("Setting HibernateMatch teamA: {}", teamA);
        this.teamA = teamA;
    }

    public String getTeamB() {
        return teamB;
    }

    public void setTeamB(String teamB) {
        logger.trace("Setting HibernateMatch teamB: {}", teamB);
        this.teamB = teamB;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        logger.trace("Setting HibernateMatch ticketPrice: {}", ticketPrice);
        this.ticketPrice = ticketPrice;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        logger.debug("Setting HibernateMatch availableSeats from {} to {}", this.availableSeats, availableSeats);
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