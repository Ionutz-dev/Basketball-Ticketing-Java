package app.repository.rest;

import app.model.Match;
import app.repository.IMatchRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MatchRestRepository implements IMatchRepository {
    private static final Logger logger = LogManager.getLogger(MatchRestRepository.class);
    private final Connection connection;

    public MatchRestRepository(Properties props) {
        logger.info("Initializing REST Match Repository");
        try {
            String url = props.getProperty("jdbc.url");
            logger.debug("Connecting to database: {}", url);
            connection = DriverManager.getConnection(url);
            logger.info("Connected to database successfully");
        } catch (SQLException e) {
            logger.error("Failed to connect to database", e);
            throw new RuntimeException("Database connection failed", e);
        }
    }

    @Override
    public List<Match> findAll() {
        logger.debug("Finding all matches");
        List<Match> matches = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Matches")) {

            logger.debug("Executing SQL: SELECT * FROM Matches");
            while (rs.next()) {
                Match match = new Match(
                        rs.getInt("id"),
                        rs.getString("teamA"),
                        rs.getString("teamB"),
                        rs.getDouble("ticketPrice"),
                        rs.getInt("availableSeats")
                );
                matches.add(match);
            }
            logger.info("Found {} matches", matches.size());
        } catch (SQLException e) {
            logger.error("Error finding all matches", e);
        }
        return matches;
    }

    @Override
    public List<Match> findAvailableMatches() {
        logger.debug("Finding available matches");
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM Matches WHERE availableSeats > 0 ORDER BY availableSeats DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            logger.debug("Executing SQL: {}", sql);
            while (rs.next()) {
                Match match = new Match(
                        rs.getInt("id"),
                        rs.getString("teamA"),
                        rs.getString("teamB"),
                        rs.getDouble("ticketPrice"),
                        rs.getInt("availableSeats")
                );
                matches.add(match);
            }
            logger.info("Found {} available matches", matches.size());
        } catch (SQLException e) {
            logger.error("Error finding available matches", e);
        }
        return matches;
    }

    @Override
    public void updateSeats(int matchId, int seatsSold) {
        logger.debug("Updating seats for match id={}, seats sold={}", matchId, seatsSold);
        String sql = "UPDATE Matches SET availableSeats = availableSeats - ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
            logger.error("Error updating seats for match id={}", matchId, e);
        }
    }

    @Override
    public Match findMatchById(int matchId) {
        logger.debug("Finding match by id={}", matchId);
        String sql = "SELECT * FROM Matches WHERE id = ?";
        Match match = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, matchId);
            logger.debug("Executing SQL: {} with param [{}]", sql, matchId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    match = new Match(
                            rs.getInt("id"),
                            rs.getString("teamA"),
                            rs.getString("teamB"),
                            rs.getDouble("ticketPrice"),
                            rs.getInt("availableSeats")
                    );
                    logger.info("Found match with id={}: {}", matchId, match);
                } else {
                    logger.warn("No match found with id={}", matchId);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding match by id={}", matchId, e);
        }
        return match;
    }

    /**
     * Create a new match in the database
     *
     * @param match The match to create (ID will be generated)
     * @return The created match with assigned ID
     */
    public Match save(Match match) {
        logger.debug("Creating new match: {}", match);
        String sql = "INSERT INTO Matches (teamA, teamB, ticketPrice, availableSeats) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, match.getTeamA());
            stmt.setString(2, match.getTeamB());
            stmt.setDouble(3, match.getTicketPrice());
            stmt.setInt(4, match.getAvailableSeats());

            logger.debug("Executing SQL: {} with params [{}, {}, {}, {}]",
                    sql, match.getTeamA(), match.getTeamB(), match.getTicketPrice(), match.getAvailableSeats());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.error("Creating match failed, no rows affected");
                return null;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    match.setId(id);
                    logger.info("Match created with ID: {}", id);
                    return match;
                } else {
                    logger.error("Creating match failed, no ID obtained");
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating match", e);
            return null;
        }
    }

    /**
     * Update an existing match
     *
     * @param matchId The ID of the match to update
     * @param match The updated match data
     * @return The updated match or null if not found
     */
    public Match update(int matchId, Match match) {
        logger.debug("Updating match with id={}: {}", matchId, match);
        String sql = "UPDATE Matches SET teamA = ?, teamB = ?, ticketPrice = ?, availableSeats = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, match.getTeamA());
            stmt.setString(2, match.getTeamB());
            stmt.setDouble(3, match.getTicketPrice());
            stmt.setInt(4, match.getAvailableSeats());
            stmt.setInt(5, matchId);

            logger.debug("Executing SQL: {} with params [{}, {}, {}, {}, {}]",
                    sql, match.getTeamA(), match.getTeamB(), match.getTicketPrice(), match.getAvailableSeats(), matchId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Successfully updated match with id={}", matchId);
                match.setId(matchId);
                return match;
            } else {
                logger.warn("No match found with id={} for update", matchId);
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error updating match with id={}", matchId, e);
            return null;
        }
    }

    /**
     * Delete a match by ID
     *
     * @param matchId The ID of the match to delete
     * @return true if the match was deleted, false otherwise
     */
    public boolean delete(int matchId) {
        logger.debug("Deleting match with id={}", matchId);
        String sql = "DELETE FROM Matches WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, matchId);
            logger.debug("Executing SQL: {} with param [{}]", sql, matchId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Successfully deleted match with id={}", matchId);
                return true;
            } else {
                logger.warn("No match found with id={} for deletion", matchId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error deleting match with id={}", matchId, e);
            return false;
        }
    }
}