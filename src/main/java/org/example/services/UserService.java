package org.example.services;

import org.example.models.User;
import org.example.repositories.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void deleteUser(String targetUserId, String loggedInUserId) {
        if (targetUserId.equals(loggedInUserId)) {
            throw new IllegalStateException("Nie możesz usunąć samego siebie.");
        }

        userRepository.deleteById(targetUserId);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika"));
    }
}