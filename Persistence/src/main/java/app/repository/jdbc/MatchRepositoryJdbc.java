package app.repository.jdbc;

import app.model.Match;
import app.repository.IMatchRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MatchRepositoryJdbc implements IMatchRepository {
    private static final Logger logger = LogManager.getLogger(MatchRepositoryJdbc.class);
    private final JdbcUtils jdbcUtils;

    public MatchRepositoryJdbc(Properties props) {
        this.jdbcUtils = new JdbcUtils(props); // inject config at construction
    }

    @Override
    public List<Match> findAll() {
        List<Match> matches = new ArrayList<>();
        try (Connection conn = jdbcUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Matches")) {

            while (rs.next()) {
                Match match = new Match(rs.getInt("id"), rs.getString("teamA"),
                        rs.getString("teamB"), rs.getDouble("ticketPrice"), rs.getInt("availableSeats"));
                matches.add(match);
            }
            logger.info("Fetched all matches");
        } catch (Exception e) {
            logger.error("Error fetching matches", e);
        }
        return matches;
    }

    @Override
    public List<Match> findAvailableMatches() {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM Matches WHERE availableSeats > 0 ORDER BY availableSeats DESC";
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Match match = new Match(rs.getInt("id"), rs.getString("teamA"),
                        rs.getString("teamB"), rs.getDouble("ticketPrice"), rs.getInt("availableSeats"));
                matches.add(match);
            }
            logger.info("Fetched available matches");
        } catch (Exception e) {
            logger.error("Error fetching available matches", e);
        }
        return matches;
    }

    @Override
    public void updateSeats(int matchId, int seatsSold) {
        String sql = "UPDATE Matches SET availableSeats = availableSeats - ? WHERE id = ?";
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, seatsSold);
            stmt.setInt(2, matchId);
            stmt.executeUpdate();
            logger.info("Updated seats for matchId=" + matchId);
        } catch (Exception e) {
            logger.error("Error updating seats", e);
        }
    }

    @Override
    public Match findMatchById(int matchId) {
        String sql = "SELECT * FROM Matches WHERE id = ?";
        Match match = null;

        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, matchId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                match = new Match(rs.getInt("id"), rs.getString("teamA"),
                        rs.getString("teamB"), rs.getDouble("ticketPrice"), rs.getInt("availableSeats"));
            }
        } catch (Exception e) {
            logger.error("Error finding match by ID", e);
        }
        return match;
    }
}
