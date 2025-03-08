package model;

public class Match {
    private int matchID;
    private String title;
    private String stage;
    private int availableSeats;
    private double price;

    public Match(int matchID, String title, String stage, int availableSeats, double price) {
        this.matchID = matchID;
        this.title = title;
        this.stage = stage;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    public int getMatchID() {
        return matchID;
    }

    public void setMatchID(int matchID) {
        this.matchID = matchID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Match{" +
                "matchID=" + matchID +
                ", title='" + title + '\'' +
                ", stage='" + stage + '\'' +
                ", price=" + price +
                ", availableSeats=" + availableSeats +
                '}';
    }
}
