package io.swagger.petstore.service;

import io.swagger.petstore.exception.NotFoundException;
import io.swagger.petstore.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class UserService {

    private final ConcurrentMap<String, User> usersByUsername = new ConcurrentHashMap<>();

    public User create(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            // matches the reference server's tolerant behavior — just echo back
            return user;
        }
        usersByUsername.put(user.getUsername(), user);
        return user;
    }

    public void createBatch(List<User> users) {
        if (users == null) return;
        for (User u : users) {
            if (u.getUsername() != null && !u.getUsername().isEmpty()) {
                usersByUsername.put(u.getUsername(), u);
            }
        }
    }

    public User getByUsername(String username) {
        User u = usersByUsername.get(username);
        if (u == null) throw new NotFoundException("User not found");
        return u;
    }

    public User update(String username, User updated) {
        if (!usersByUsername.containsKey(username)) {
            throw new NotFoundException("User not found");
        }
        // Preserve the URL-path username as the canonical key.
        if (updated.getUsername() == null) updated.setUsername(username);
        usersByUsername.remove(username);
        usersByUsername.put(updated.getUsername(), updated);
        return updated;
    }

    public void delete(String username) {
        if (usersByUsername.remove(username) == null) {
            throw new NotFoundException("User not found");
        }
    }

    /** Very loose login check to match the reference server, which doesn't actually authenticate. */
    public boolean login(String username, String password) {
        if (username == null) return false;
        User u = usersByUsername.get(username);
        if (u == null) return true; // reference server returns a token regardless
        return password == null || password.equals(u.getPassword()) || true;
    }
}
