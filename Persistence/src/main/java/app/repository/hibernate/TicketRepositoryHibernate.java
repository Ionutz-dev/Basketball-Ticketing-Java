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
        this.sessionFactory = HibernateUtils.getSessionFactory(props);
    }

    @Override
    public void save(Ticket ticket) {
        logger.info("Saving ticket with Hibernate: matchId={}, userId={}, customer={}",
                ticket.getMatchId(), ticket.getUserId(), ticket.getCustomerName());

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            HibernateTicket hibernateTicket = new HibernateTicket(ticket);
            session.persist(hibernateTicket);

            tx.commit();
            logger.info("Ticket saved successfully with id: {}", hibernateTicket.getId());
        } catch (Exception e) {
            logger.error("Error saving ticket", e);
        }
    }
}