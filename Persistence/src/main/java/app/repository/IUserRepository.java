package app.repository;

import app.model.User;

public interface IUserRepository {
    User findByUsernameAndPassword(String username, String password);
}
