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
        logger.info("Initializing JDBC Match Repository");
        this.jdbcUtils = new JdbcUtils(props);
    }

    @Override
    public List<Match> findAll() {
        logger.debug("Finding all matches");
        List<Match> matches = new ArrayList<>();
        try (Connection conn = jdbcUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Matches")) {

            logger.debug("Executing SQL: SELECT * FROM Matches");
            while (rs.next()) {
                Match match = new Match(rs.getInt("id"), rs.getString("teamA"),
                        rs.getString("teamB"), rs.getDouble("ticketPrice"), rs.getInt("availableSeats"));
                matches.add(match);
            }
            logger.info("Found {} matches", matches.size());
        } catch (SQLException e) {
            logger.error("SQL error finding all matches", e);
        } catch (Exception e) {
            logger.error("Unexpected error finding all matches", e);
        }
        return matches;
    }

    @Override
    public List<Match> findAvailableMatches() {
        logger.debug("Finding available matches");
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM Matches WHERE availableSeats > 0 ORDER BY availableSeats DESC";

        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            logger.debug("Executing SQL: {}", sql);
            while (rs.next()) {
                Match match = new Match(rs.getInt("id"), rs.getString("teamA"),
                        rs.getString("teamB"), rs.getDouble("ticketPrice"), rs.getInt("availableSeats"));
                matches.add(match);
            }
            logger.info("Found {} available matches", matches.size());
        } catch (SQLException e) {
            logger.error("SQL error finding available matches", e);
        } catch (Exception e) {
            logger.error("Unexpected error finding available matches", e);
        }
        return matches;
    }

    @Override
    public void updateSeats(int matchId, int seatsSold) {
        logger.debug("Updating seats for match id={}, seats sold={}", matchId, seatsSold);
        String sql = "UPDATE Matches SET availableSeats = availableSeats - ? WHERE id = ?";

        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, seatsSold);
            stmt.setInt(2, matchId);

            logger.debug("Executing SQL: {} with params [{}, {}]", sql, seatsSold, matchId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Successfully updated seats for match id={}, rows affected={}", matchId, rowsAffected);
            } else {
                logger.warn("No rows affected when updating seats for match id={}", matchId);
            }
        } catch (SQLException e) {
            logger.error("SQL error updating seats for match id={}", matchId, e);
        } catch (Exception e) {
            logger.error("Unexpected error updating seats for match id={}", matchId, e);
        }
    }

    @Override
    public Match findMatchById(int matchId) {
        logger.debug("Finding match by id={}", matchId);
        String sql = "SELECT * FROM Matches WHERE id = ?";
        Match match = null;

        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matchId);
            logger.debug("Executing SQL: {} with param [{}]", sql, matchId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                match = new Match(rs.getInt("id"), rs.getString("teamA"),
                        rs.getString("teamB"), rs.getDouble("ticketPrice"), rs.getInt("availableSeats"));
                logger.info("Found match with id={}: {}", matchId, match);
            } else {
                logger.warn("No match found with id={}", matchId);
            }
        } catch (SQLException e) {
            logger.error("SQL error finding match by id={}", matchId, e);
        } catch (Exception e) {
            logger.error("Unexpected error finding match by id={}", matchId, e);
        }
        return match;
    }
}