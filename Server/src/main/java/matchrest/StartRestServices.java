package matchrest;

import app.repository.IMatchRepository;
import app.repository.ITicketRepository;
import app.repository.IUserRepository;
import app.repository.hibernate.MatchRepositoryHibernate;
import app.repository.hibernate.TicketRepositoryHibernate;
import app.repository.hibernate.UserRepositoryHibernate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

// Remove the redundant @ComponentScan - it's already included in @SpringBootApplication
@SpringBootApplication
public class StartRestServices {
    public static void main(String[] args) {
        SpringApplication.run(StartRestServices.class, args);
    }

    @Bean(name = "appProperties")
    @Primary  // Mark this as the primary Properties bean
    public Properties getDBProperties() {
        Properties props = new Properties();
        try {
            URL resource = StartRestServices.class.getClassLoader().getResource("appserver.properties");
            System.out.println("Searching appserver.properties in directory " + resource);
            if (resource != null) {
                props.load(new FileReader(resource.getFile()));
            } else {
                System.err.println("Configuration file appserver.properties not found");
            }
        } catch (IOException e) {
            System.err.println("Error loading configuration file: " + e.getMessage());
        }
        return props;
    }

    @Bean
    public IMatchRepository matchRepository(@Qualifier("appProperties") Properties props) {
        return new MatchRepositoryHibernate(props);
    }

    @Bean
    public ITicketRepository ticketRepository(@Qualifier("appProperties") Properties props) {
        return new TicketRepositoryHibernate(props);
    }

    @Bean
    public IUserRepository userRepository(@Qualifier("appProperties") Properties props) {
        return new UserRepositoryHibernate(props);
    }
}