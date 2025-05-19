package app.rest;

import app.repository.IMatchRepository;
import app.repository.rest.MatchRestRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

@SpringBootApplication
public class StartRestServices implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(StartRestServices.class, args);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Bean(name = "appProperties")
    @Primary
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
    public IMatchRepository matchRepository(Properties props) {
        return new MatchRestRepository(props);
    }

    @Bean
    public MatchRestController matchRestController(Properties props) {
        return new MatchRestController(props);
    }
}