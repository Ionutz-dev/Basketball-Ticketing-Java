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
        logger.info("Initializing Hibernate Match Repository");
        this.sessionFactory = HibernateUtils.getSessionFactory(props);
    }

    @Override
    public List<Match> findAll() {
        logger.debug("Finding all matches with Hibernate");
        List<Match> matches = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            logger.debug("Executing HQL: FROM HibernateMatch");
            Query<HibernateMatch> query = session.createQuery("FROM HibernateMatch", HibernateMatch.class);
            List<HibernateMatch> hibernateMatches = query.list();

            logger.debug("Converting {} HibernateMatch entities to Match objects", hibernateMatches.size());
            matches = hibernateMatches.stream()
                    .map(HibernateMatch::toMatch)
                    .collect(Collectors.toList());

            logger.info("Found {} matches", matches.size());
        } catch (Exception e) {
            logger.error("Error finding all matches with Hibernate", e);
        }

        return matches;
    }

    @Override
    public List<Match> findAvailableMatches() {
        logger.debug("Finding available matches with Hibernate");
        List<Match> matches = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM HibernateMatch WHERE availableSeats > 0 ORDER BY availableSeats DESC";
            logger.debug("Executing HQL: {}", hql);

            Query<HibernateMatch> query = session.createQuery(hql, HibernateMatch.class);
            List<HibernateMatch> hibernateMatches = query.list();

            logger.debug("Converting {} HibernateMatch entities to Match objects", hibernateMatches.size());
            matches = hibernateMatches.stream()
                    .map(HibernateMatch::toMatch)
                    .collect(Collectors.toList());

            logger.info("Found {} available matches", matches.size());
        } catch (Exception e) {
            logger.error("Error finding available matches with Hibernate", e);
        }

        return matches;
    }

    @Override
    public void updateSeats(int matchId, int seatsSold) {
        logger.debug("Updating seats for match id={}, seats sold={} with Hibernate", matchId, seatsSold);

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            logger.debug("Retrieving match with id={}", matchId);
            HibernateMatch match = session.get(HibernateMatch.class, matchId);

            if (match != null) {
                int currentSeats = match.getAvailableSeats();
                int newSeats = currentSeats - seatsSold;

                logger.debug("Updating available seats from {} to {}", currentSeats, newSeats);
                match.setAvailableSeats(newSeats);
                session.persist(match);

                logger.debug("Committing transaction");
                tx.commit();

                logger.info("Successfully updated seats for match id={}", matchId);
            } else {
                logger.warn("No match found with id={} for seat update", matchId);
                tx.rollback();
            }
        } catch (Exception e) {
            logger.error("Error updating seats for match id={}", matchId, e);
        }
    }

    @Override
    public Match findMatchById(int matchId) {
        logger.debug("Finding match by id={} with Hibernate", matchId);
        Match match = null;

        try (Session session = sessionFactory.openSession()) {
            logger.debug("Retrieving HibernateMatch with id={}", matchId);
            HibernateMatch hibernateMatch = session.get(HibernateMatch.class, matchId);

            if (hibernateMatch != null) {
                match = hibernateMatch.toMatch();
                logger.info("Found match with id={}: {}", matchId, match);
            } else {
                logger.warn("No match found with id={}", matchId);
            }
        } catch (Exception e) {
            logger.error("Error finding match by id={} with Hibernate", matchId, e);
        }

        return match;
    }
}