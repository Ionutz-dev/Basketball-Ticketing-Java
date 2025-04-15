package app.repository;

import app.model.Ticket;

public interface ITicketRepository {
    void save(Ticket ticket);
}
