package tickets.repository;

import tickets.model.Ticket;

public interface ITicketRepository {
    void save(Ticket ticket);
}
