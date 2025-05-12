package app.repository.jdbc;

import app.repository.ITicketRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class TicketRepositoryJdbc implements ITicketRepository {
    private static final Logger logger = LogManager.getLogger(TicketRepositoryJdbc.class);
    private final JdbcUtils jdbcUtils;

    public TicketRepositoryJdbc(Properties props) {
        logger.info("Initializing JDBC Ticket Repository");
        this.jdbcUtils = new JdbcUtils(props);
    }

    @Override
    public void save(Ticket ticket) {
        logger.debug("Saving ticket: matchId={}, userId={}, customer={}, seats={}",
                ticket.getMatchId(), ticket.getUserId(), ticket.getCustomerName(), ticket.getSeatsSold());

        String sql = "INSERT INTO Tickets (matchId, userId, customerName, seatsSold) VALUES (?, ?, ?, ?)";

        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticket.getMatchId());
            stmt.setInt(2, ticket.getUserId());
            stmt.setString(3, ticket.getCustomerName());
            stmt.setInt(4, ticket.getSeatsSold());

            logger.debug("Executing SQL: {} with params [{}, {}, {}, {}]",
                    sql, ticket.getMatchId(), ticket.getUserId(), ticket.getCustomerName(), ticket.getSeatsSold());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Successfully saved ticket for match id={}, customer={}",
                        ticket.getMatchId(), ticket.getCustomerName());
            } else {
                logger.warn("No rows affected when saving ticket for match id={}", ticket.getMatchId());
            }
        } catch (SQLException e) {
            logger.error("SQL error saving ticket", e);
        } catch (Exception e) {
            logger.error("Unexpected error saving ticket", e);
        }
    }
}