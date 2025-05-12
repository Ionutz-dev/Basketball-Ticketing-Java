package app.repository.hibernate;

import app.model.Ticket;
import app.model.hibernate.HibernateTicket;
import app.repository.ITicketRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Properties;

public class TicketRepositoryHibernate implements ITicketRepository {
    private static final Logger logger = LogManager.getLogger(TicketRepositoryHibernate.class);
    private final SessionFactory sessionFactory;

    public TicketRepositoryHibernate(Properties props) {
        logger.info("Initializing Hibernate Ticket Repository");
        this.sessionFactory = HibernateUtils.getSessionFactory(props);
    }

    @Override
    public void save(Ticket ticket) {
        logger.debug("Saving ticket with Hibernate: matchId={}, userId={}, customer={}, seats={}",
                ticket.getMatchId(), ticket.getUserId(), ticket.getCustomerName(), ticket.getSeatsSold());

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            logger.debug("Converting Ticket to HibernateTicket");
            HibernateTicket hibernateTicket = new HibernateTicket(ticket);

            logger.debug("Persisting HibernateTicket entity");
            session.persist(hibernateTicket);

            logger.debug("Committing transaction");
            tx.commit();

            logger.info("Ticket saved successfully with id: {}", hibernateTicket.getId());
        } catch (Exception e) {
            logger.error("Error saving ticket with Hibernate", e);
        }
    }
}