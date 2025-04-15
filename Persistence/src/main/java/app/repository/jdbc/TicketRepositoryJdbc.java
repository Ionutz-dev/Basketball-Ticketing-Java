package app.repository.jdbc;

import app.repository.ITicketRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;

public class TicketRepositoryJdbc implements ITicketRepository {
    private static final Logger logger = LogManager.getLogger(TicketRepositoryJdbc.class);
    private final JdbcUtils jdbcUtils;

    public TicketRepositoryJdbc(Properties props) {
        this.jdbcUtils = new JdbcUtils(props);
    }

    @Override
    public void save(Ticket ticket) {
        String sql = "INSERT INTO Tickets (matchId, userId, customerName, seatsSold) VALUES (?, ?, ?, ?)";

        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticket.getMatchId());
            stmt.setInt(2, ticket.getUserId());
            stmt.setString(3, ticket.getCustomerName());
            stmt.setInt(4, ticket.getSeatsSold());
            stmt.executeUpdate();

            logger.info("Saved ticket - matchId=" + ticket.getMatchId() + ", userId=" + ticket.getUserId() + ", customer=" + ticket.getCustomerName());
        } catch (Exception e) {
            logger.error("Error saving ticket", e);
        }
    }
}
