package app.repository.hibernate;

import app.model.Match;
import app.model.hibernate.HibernateMatch;
import app.repository.IMatchRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class MatchRepositoryHibernate implements IMatchRepository {
    private static final Logger logger = LogManager.getLogger(MatchRepositoryHibernate.class);
    private final SessionFactory sessionFactory;

    public MatchRepositoryHibernate(Properties props) {
        this.sessionFactory = HibernateUtils.getSessionFactory(props);
    }

    @Override
    public List<Match> findAll() {
        logger.info("Fetching all matches with Hibernate");
        List<Match> matches = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            Query<HibernateMatch> query = session.createQuery("FROM HibernateMatch", HibernateMatch.class);
            List<HibernateMatch> hibernateMatches = query.list();

            matches = hibernateMatches.stream()
                    .map(HibernateMatch::toMatch)
                    .collect(Collectors.toList());

            logger.info("Fetched {} matches", matches.size());
        } catch (Exception e) {
            logger.error("Error fetching all matches", e);
        }

        return matches;
    }

    @Override
    public List<Match> findAvailableMatches() {
        logger.info("Fetching available matches with Hibernate");
        List<Match> matches = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            Query<HibernateMatch> query = session.createQuery(
                    "FROM HibernateMatch WHERE availableSeats > 0 ORDER BY availableSeats DESC",
                    HibernateMatch.class
            );

            List<HibernateMatch> hibernateMatches = query.list();

            matches = hibernateMatches.stream()
                    .map(HibernateMatch::toMatch)
                    .collect(Collectors.toList());

            logger.info("Fetched {} available matches", matches.size());
        } catch (Exception e) {
            logger.error("Error fetching available matches", e);
        }

        return matches;
    }

    @Override
    public void updateSeats(int matchId, int seatsSold) {
        logger.info("Updating seats for match {} with Hibernate, seats sold: {}", matchId, seatsSold);

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            HibernateMatch match = session.get(HibernateMatch.class, matchId);
            if (match != null) {
                match.setAvailableSeats(match.getAvailableSeats() - seatsSold);
                session.persist(match);
                logger.info("Match seats updated, new available seats: {}", match.getAvailableSeats());
            } else {
                logger.warn("Match with id {} not found", matchId);
            }

            tx.commit();
        } catch (Exception e) {
            logger.error("Error updating seats for match {}", matchId, e);
        }
    }

    @Override
    public Match findMatchById(int matchId) {
        logger.info("Finding match by id {} with Hibernate", matchId);
        Match match = null;

        try (Session session = sessionFactory.openSession()) {
            HibernateMatch hibernateMatch = session.get(HibernateMatch.class, matchId);

            if (hibernateMatch != null) {
                match = hibernateMatch.toMatch();
                logger.info("Found match: {}", match);
            } else {
                logger.warn("Match with id {} not found", matchId);
            }
        } catch (Exception e) {
            logger.error("Error finding match by id {}", matchId, e);
        }

        return match;
    }
}