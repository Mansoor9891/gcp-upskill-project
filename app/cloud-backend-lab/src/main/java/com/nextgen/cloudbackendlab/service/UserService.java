package com.nextgen.cloudbackendlab.service;

import com.nextgen.cloudbackendlab.entity.User;
import com.nextgen.cloudbackendlab.event.UserEvent;
import com.nextgen.cloudbackendlab.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserEventPublisher userEventPublisher;

    public UserService(UserRepository userRepository,
                       UserEventPublisher userEventPublisher) {
        this.userRepository = userRepository;
        this.userEventPublisher = userEventPublisher;
    }

    public User createUser(User user) {
        User savedUser = userRepository.save(user);

        UserEvent event = new UserEvent(
                "USER_CREATED",
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail()
        );

        userEventPublisher.publishUserCreated(event);

        return savedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());

        User savedUser = userRepository.save(existingUser);

        UserEvent event = new UserEvent(
                "USER_UPDATED",
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail()
        );

        userEventPublisher.publishUserUpdated(event);

        return savedUser;
    }

    public void deleteUser(Long id) {
        User existingUser = getUserById(id);
        userRepository.delete(existingUser);
    }
}