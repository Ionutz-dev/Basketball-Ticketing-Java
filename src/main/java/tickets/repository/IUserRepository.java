package tickets.repository;

import tickets.model.User;

public interface IUserRepository {
    User findByUsernameAndPassword(String username, String password);
}
