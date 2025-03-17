import repository.MatchRepository;
import model.Match;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        MatchRepository repo = new MatchRepository();
        List<Match> matches = repo.findAll();
        for (Match match : matches) {
            System.out.println(match.getTeamA() + " vs " + match.getTeamB());
        }
    }
}
