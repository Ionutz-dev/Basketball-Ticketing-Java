package app.network.dto;

import app.model.Match;
import app.model.Ticket;
import app.model.User;

public class DTOUtils {
    public static MatchDTO getDTO(Match match) {
        return new MatchDTO(match.getId(), match.getTeamA(), match.getTeamB(), match.getTicketPrice(), match.getAvailableSeats());
    }

    public static Match getFromDTO(MatchDTO dto) {
        return new Match(dto.getId(), dto.getTeamA(), dto.getTeamB(), dto.getTicketPrice(), dto.getAvailableSeats());
    }

    public static TicketDTO getDTO(Ticket ticket) {
        return new TicketDTO(ticket.getId(), ticket.getMatchId(), ticket.getUserId(), ticket.getCustomerName(), ticket.getSeatsSold());
    }

    public static Ticket getFromDTO(TicketDTO dto) {
        return new Ticket(dto.getId(), dto.getMatchId(), dto.getUserId(), dto.getCustomerName(), dto.getSeatsSold());
    }

    public static UserDTO getDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getPassword());
    }

    public static User getFromDTO(UserDTO dto) {
        return new User(dto.getId(), dto.getUsername(), dto.getPassword());
    }
}
