package tickets.repository;

import tickets.model.Ticket;
import tickets.utils.DBUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TicketRepository implements ITicketRepository {
    private static final Logger logger = LogManager.getLogger(TicketRepository.class);

    @Override
    public void save(Ticket ticket) {
        String sql = "INSERT INTO Tickets (matchId, userId, customerName, seatsSold) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticket.getMatchId());
            stmt.setInt(2, ticket.getUserId());  // NEW: save userId
            stmt.setString(3, ticket.getCustomerName());
            stmt.setInt(4, ticket.getSeatsSold());
            stmt.executeUpdate();

            logger.info("Saved ticket - matchId=" + ticket.getMatchId() + ", userId=" + ticket.getUserId() + ", customer=" + ticket.getCustomerName());
        } catch (Exception e) {
            logger.error("Error saving ticket", e);
        }
    }
}
