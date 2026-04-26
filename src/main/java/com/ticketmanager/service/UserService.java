package com.ticketmanager.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ticketmanager.exception.UserNotFounException;
import com.ticketmanager.model.User;
import com.ticketmanager.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getUserById(String id) {
        return repository.findById(id).orElseThrow(() -> new UserNotFounException("User not found"));
    }

    // public User createUser(User user) {

    // }

    public void deleteUser(String id) {
        if (!repository.existsById(id)) {
            throw new UserNotFounException("User not found");
        }
        repository.deleteById(id);
    }

    public User updateUser(String id, User updatedUser) {
        return repository.findById(id)
                .map(user -> {
                    user.setUsername(updatedUser.getUsername());
                    user.setEmail(updatedUser.getEmail());
                    return repository.save(user);
                })
                .orElseThrow(() -> new UserNotFounException("User not found"));
    }

    public User updatePassword(String id, String oldPassword, String newPassword) {
        return repository.findById(id)
                .map(user -> {
                    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                        throw new RuntimeException("Incorrect password");
                    }
                    user.setPassword(passwordEncoder.encode(newPassword));
                    return repository.save(user);
                })
                .orElseThrow(() -> new UserNotFounException("User not found"));
    }

}
