package com.dnu.service;

import com.dnu.entity.User;
import com.dnu.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(String firstName, String lastName, String email) {
        var user = new User(firstName, lastName, email);
        userRepository.save(user);
        return user;
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    @Transactional
    public void editUser(Long id, String newFirstName, String newLastName, String email) {
        User user = userRepository.findById(id).orElseThrow();
        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        user.setEmail(email);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
