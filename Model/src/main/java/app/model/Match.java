package app.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Match {
    private static final Logger logger = LogManager.getLogger(Match.class);

    private int id;
    private String teamA;
    private String teamB;
    private double ticketPrice;
    private int availableSeats;

    public Match() {
        logger.debug("Creating empty Match instance");
    }

    public Match(int id, String teamA, String teamB, double ticketPrice, int availableSeats) {
        logger.debug("Creating Match instance: id={}, teamA={}, teamB={}, price={}, seats={}",
                id, teamA, teamB, ticketPrice, availableSeats);
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
        logger.trace("Setting Match id: {}", id);
        this.id = id;
    }

    public String getTeamA() {
        return teamA;
    }

    public void setTeamA(String teamA) {
        logger.trace("Setting Match teamA: {}", teamA);
        this.teamA = teamA;
    }

    public String getTeamB() {
        return teamB;
    }

    public void setTeamB(String teamB) {
        logger.trace("Setting Match teamB: {}", teamB);
        this.teamB = teamB;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        logger.trace("Setting Match ticketPrice: {}", ticketPrice);
        this.ticketPrice = ticketPrice;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        logger.debug("Setting Match availableSeats from {} to {}", this.availableSeats, availableSeats);
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