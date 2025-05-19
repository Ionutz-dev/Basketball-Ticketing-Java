package matchclient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MatchClient {
    private static final String URL = "http://localhost:8080/basketball/api/matches";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- Basketball Match REST Client ---");
            System.out.println("1. Get all matches");
            System.out.println("2. Get match by ID");
            System.out.println("3. Create match");
            System.out.println("4. Update match");
            System.out.println("5. Delete match");
            System.out.println("0. Exit");
            System.out.print("\nSelect an option: ");

            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (option) {
                    case 1:
                        getAllMatches();
                        break;
                    case 2:
                        System.out.print("Enter match ID: ");
                        int id = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        getMatchById(id);
                        break;
                    case 3:
                        createMatch(scanner);
                        break;
                    case 4:
                        updateMatch(scanner);
                        break;
                    case 5:
                        System.out.print("Enter match ID to delete: ");
                        int deleteId = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        deleteMatch(deleteId);
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
        httpClient.close();
    }

    private static void getAllMatches() throws IOException {
        HttpGet request = new HttpGet(URL);
        request.addHeader("Accept", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String json = EntityUtils.toString(response.getEntity());
            Match[] matches = gson.fromJson(json, Match[].class);

            System.out.println("\n--- All Matches ---");
            for (Match match : matches) {
                System.out.println(match);
            }
        }
    }

    private static void getMatchById(int id) throws IOException {
        HttpGet request = new HttpGet(URL + "/" + id);
        request.addHeader("Accept", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String json = EntityUtils.toString(response.getEntity());
                Match match = gson.fromJson(json, Match.class);
                System.out.println("\n--- Match Details ---");
                System.out.println(match);
            } else if (statusCode == 404) {
                System.out.println("Match not found with ID: " + id);
            } else {
                System.out.println("Error: " + statusCode);
            }
        }
    }

    private static void createMatch(Scanner scanner) throws IOException {
        Match match = new Match();

        System.out.print("Enter Team A: ");
        match.setTeamA(scanner.nextLine());

        System.out.print("Enter Team B: ");
        match.setTeamB(scanner.nextLine());

        System.out.print("Enter Ticket Price: ");
        match.setTicketPrice(scanner.nextDouble());
        scanner.nextLine(); // Consume newline

        System.out.print("Enter Available Seats: ");
        match.setAvailableSeats(scanner.nextInt());
        scanner.nextLine(); // Consume newline

        HttpPost request = new HttpPost(URL);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Accept", "application/json");

        String json = gson.toJson(match);
        request.setEntity(new StringEntity(json));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200 || statusCode == 201) {
                String responseJson = EntityUtils.toString(response.getEntity());
                Match createdMatch = gson.fromJson(responseJson, Match.class);
                System.out.println("\n--- Created Match ---");
                System.out.println(createdMatch);
            } else {
                System.out.println("Error creating match: " + statusCode);
            }
        }
    }

    private static void updateMatch(Scanner scanner) throws IOException {
        System.out.print("Enter match ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // First get current match
        HttpGet getRequest = new HttpGet(URL + "/" + id);
        getRequest.addHeader("Accept", "application/json");

        try (CloseableHttpResponse getResponse = httpClient.execute(getRequest)) {
            int statusCode = getResponse.getStatusLine().getStatusCode();
            if (statusCode == 404) {
                System.out.println("Match not found with ID: " + id);
                return;
            } else if (statusCode != 200) {
                System.out.println("Error getting match: " + statusCode);
                return;
            }

            String json = EntityUtils.toString(getResponse.getEntity());
            Match match = gson.fromJson(json, Match.class);

            System.out.print("Enter Team A (current: " + match.getTeamA() + "): ");
            String teamA = scanner.nextLine();
            if (!teamA.trim().isEmpty()) {
                match.setTeamA(teamA);
            }

            System.out.print("Enter Team B (current: " + match.getTeamB() + "): ");
            String teamB = scanner.nextLine();
            if (!teamB.trim().isEmpty()) {
                match.setTeamB(teamB);
            }

            System.out.print("Enter Ticket Price (current: " + match.getTicketPrice() + "): ");
            String priceStr = scanner.nextLine();
            if (!priceStr.trim().isEmpty()) {
                match.setTicketPrice(Double.parseDouble(priceStr));
            }

            System.out.print("Enter Available Seats (current: " + match.getAvailableSeats() + "): ");
            String seatsStr = scanner.nextLine();
            if (!seatsStr.trim().isEmpty()) {
                match.setAvailableSeats(Integer.parseInt(seatsStr));
            }

            HttpPut updateRequest = new HttpPut(URL + "/" + id);
            updateRequest.addHeader("Content-Type", "application/json");
            updateRequest.addHeader("Accept", "application/json");

            String updateJson = gson.toJson(match);
            updateRequest.setEntity(new StringEntity(updateJson));

            try (CloseableHttpResponse updateResponse = httpClient.execute(updateRequest)) {
                int updateStatusCode = updateResponse.getStatusLine().getStatusCode();
                if (updateStatusCode == 200) {
                    String responseJson = EntityUtils.toString(updateResponse.getEntity());
                    Match updatedMatch = gson.fromJson(responseJson, Match.class);
                    System.out.println("\n--- Updated Match ---");
                    System.out.println(updatedMatch);
                } else {
                    System.out.println("Error updating match: " + updateStatusCode);
                }
            }
        }
    }

    private static void deleteMatch(int id) throws IOException {
        HttpDelete request = new HttpDelete(URL + "/" + id);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 204) {
                System.out.println("Match deleted successfully!");
            } else if (statusCode == 404) {
                System.out.println("Match not found with ID: " + id);
            } else {
                System.out.println("Error deleting match: " + statusCode);
            }
        }
    }

    // Inner class for Match model
    static class Match {
        private int id;
        private String teamA;
        private String teamB;
        private double ticketPrice;
        private int availableSeats;

        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getTeamA() { return teamA; }
        public void setTeamA(String teamA) { this.teamA = teamA; }

        public String getTeamB() { return teamB; }
        public void setTeamB(String teamB) { this.teamB = teamB; }

        public double getTicketPrice() { return ticketPrice; }
        public void setTicketPrice(double ticketPrice) { this.ticketPrice = ticketPrice; }

        public int getAvailableSeats() { return availableSeats; }
        public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

        @Override
        public String toString() {
            return "Match{id=" + id + ", " + teamA + " vs " + teamB +
                    ", price=" + ticketPrice + ", seats=" + availableSeats + "}";
        }
    }
}