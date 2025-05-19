package app.rest;

import app.model.Match;
import app.repository.rest.MatchRestRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping("basketball/api/matches")
public class MatchRestController {
    private static final Logger logger = LogManager.getLogger(MatchRestController.class);
    private final MatchRestRepository matchRepository;

    public MatchRestController(Properties props) {
        logger.info("Initializing MatchRestController");
        this.matchRepository = new MatchRestRepository(props);
    }

    @GetMapping("/test")
    public String test(@RequestParam(value = "name", defaultValue = "Hello") String name) {
        return name.toUpperCase();
    }

    @GetMapping
    public Collection<Match> getAll() {
        logger.info("REST request to get all matches");
        List<Match> matches = matchRepository.findAll();
        logger.debug("Returning {} matches", matches.size());
        return matches;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(name = "id") Integer id) {
        logger.info("REST request to get match with id={}", id);
        Match match = matchRepository.findMatchById(id);

        if (match == null) {
            logger.warn("Match with id={} not found", id);
            return new ResponseEntity<String>("Match not found", HttpStatus.NOT_FOUND);
        } else {
            logger.debug("Found match: {}", match);
            return new ResponseEntity<Match>(match, HttpStatus.OK);
        }
    }

    @PostMapping
    public Match create(@RequestBody Match match) {
        logger.info("REST request to create match: {}", match);

        match.setId(0);

        Match createdMatch = matchRepository.save(match);

        if (createdMatch != null) {
            logger.info("Match created with id={}", createdMatch.getId());
            return createdMatch;
        } else {
            logger.error("Failed to create match");
            throw new RuntimeException("Failed to create match");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Integer id, @RequestBody Match match) {
        logger.info("REST request to update match with id={}", id);

        Match existingMatch = matchRepository.findMatchById(id);
        if (existingMatch == null) {
            logger.warn("Match with id={} not found for update", id);
            return new ResponseEntity<String>("Match not found", HttpStatus.NOT_FOUND);
        }

        match.setId(id);
        Match updatedMatch = matchRepository.update(id, match);

        if (updatedMatch != null) {
            logger.info("Match with id={} updated successfully", id);
            return new ResponseEntity<Match>(updatedMatch, HttpStatus.OK);
        } else {
            logger.error("Error updating match with id={}", id);
            return new ResponseEntity<String>("Error updating match", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Integer id) {
        logger.info("REST request to delete match with id={}", id);

        Match existingMatch = matchRepository.findMatchById(id);
        if (existingMatch == null) {
            logger.warn("Match with id={} not found for deletion", id);
            return new ResponseEntity<String>("Match not found", HttpStatus.NOT_FOUND);
        }

        boolean deleted = matchRepository.delete(id);

        if (deleted) {
            logger.info("Match with id={} deleted successfully", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logger.error("Error deleting match with id={}", id);
            return new ResponseEntity<String>("Error deleting match", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}