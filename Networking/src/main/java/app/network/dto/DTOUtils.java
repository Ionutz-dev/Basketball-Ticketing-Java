package app.network.dto;

import app.model.Match;
import app.model.Ticket;
import app.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DTOUtils {
    private static final Logger logger = LogManager.getLogger(DTOUtils.class);

    public static MatchDTO getDTO(Match match) {
        logger.trace("Converting Match to MatchDTO: id={}", match.getId());
        return new MatchDTO(match.getId(), match.getTeamA(), match.getTeamB(), match.getTicketPrice(), match.getAvailableSeats());
    }

    public static Match getFromDTO(MatchDTO dto) {
        logger.trace("Converting MatchDTO to Match: id={}", dto.getId());
        return new Match(dto.getId(), dto.getTeamA(), dto.getTeamB(), dto.getTicketPrice(), dto.getAvailableSeats());
    }

    public static TicketDTO getDTO(Ticket ticket) {
        logger.trace("Converting Ticket to TicketDTO: id={}", ticket.getId());
        return new TicketDTO(ticket.getId(), ticket.getMatchId(), ticket.getUserId(), ticket.getCustomerName(), ticket.getSeatsSold());
    }

    public static Ticket getFromDTO(TicketDTO dto) {
        logger.trace("Converting TicketDTO to Ticket: id={}", dto.getId());
        return new Ticket(dto.getId(), dto.getMatchId(), dto.getUserId(), dto.getCustomerName(), dto.getSeatsSold());
    }

    public static UserDTO getDTO(User user) {
        logger.trace("Converting User to UserDTO: id={}, username={}", user.getId(), user.getUsername());
        return new UserDTO(user.getId(), user.getUsername(), user.getPassword());
    }

    public static User getFromDTO(UserDTO dto) {
        logger.trace("Converting UserDTO to User: id={}, username={}", dto.getId(), dto.getUsername());
        return new User(dto.getId(), dto.getUsername(), dto.getPassword());
    }
}