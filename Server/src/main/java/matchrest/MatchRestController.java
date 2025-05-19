package matchrest;

import app.model.Match;
import app.repository.IMatchRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("basketball/api/matches")
public class MatchRestController {
    private final IMatchRepository matchRepository;

    public MatchRestController(IMatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @GetMapping("/test")
    public String test(@RequestParam(value = "name", defaultValue = "Hello") String name) {
        return name.toUpperCase();
    }

    @PostMapping
    public Match create(@RequestBody Match match) {
        System.out.println("Creating match: " + match);
        match.setId(0);
        List<Match> allMatches = matchRepository.findAll();
        int maxId = 0;
        for (Match m : allMatches) {
            if (m.getId() > maxId) {
                maxId = m.getId();
            }
        }
        match.setId(maxId + 1);

        System.out.println("Match created with ID: " + match.getId());
        return match;
    }

    @GetMapping
    public Collection<Match> getAll() {
        System.out.println("Getting all matches");
        return matchRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(name = "id") Integer id) {
        System.out.println("Get match by id: " + id);
        Match match = matchRepository.findMatchById(id);
        if (match == null)
            return new ResponseEntity<String>("Match not found", HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<Match>(match, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Integer id, @RequestBody Match match) {
        System.out.println("Updating match with id: " + id);
        Match existingMatch = matchRepository.findMatchById(id);
        if (existingMatch == null) {
            return new ResponseEntity<String>("Match not found", HttpStatus.NOT_FOUND);
        }

        // Update fields
        existingMatch.setTeamA(match.getTeamA());
        existingMatch.setTeamB(match.getTeamB());
        existingMatch.setTicketPrice(match.getTicketPrice());
        existingMatch.setAvailableSeats(match.getAvailableSeats());

        int seatsChange = existingMatch.getAvailableSeats() - match.getAvailableSeats();
        if (seatsChange > 0) {
            matchRepository.updateSeats(id, seatsChange);
        }

        return new ResponseEntity<Match>(existingMatch, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Integer id) {
        System.out.println("Deleting match with id: " + id);
        Match match = matchRepository.findMatchById(id);
        if (match == null) {
            return new ResponseEntity<String>("Match not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}