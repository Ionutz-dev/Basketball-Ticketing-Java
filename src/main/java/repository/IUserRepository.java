package repository;

import model.User;

import java.util.Optional;

public interface IUserRepository {
    Optional<User> findByUsername(String username);
}
