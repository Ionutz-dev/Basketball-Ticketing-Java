package tickets.repository;

import tickets.model.User;

import java.util.Optional;

public interface IUserRepository {
    Optional<User> findByUsername(String username);
}
