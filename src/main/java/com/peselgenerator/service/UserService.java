package com.peselgenerator.service;

import com.peselgenerator.dto.RegisterRequest;
import com.peselgenerator.entity.User;
import com.peselgenerator.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for user management and authentication.
 * Implements {@link UserDetailsService} to integrate with Spring Security.
 */
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user in the system.
     *
     * @param registerRequest DTO containing user registration details.
     * @return the saved {@link User} entity.
     * @throws IllegalArgumentException if the email exists or passwords do not match.
     */
    @Transactional
    public User register(RegisterRequest registerRequest) {

        // check if email exists
        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email exists");
        }

        // check if passwords are the same
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("Different passwords!");
        }

        // create new user
        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        return userRepository.save(user);
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email to search for.
     * @return the found {@link User}.
     * @throws UsernameNotFoundException if the user is not found.
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User is not found: " + email));
    }

    /**
     * Loads user details by username (email) for Spring Security authentication.
     *
     * @param email the username/email identifying the user.
     * @return the {@link UserDetails} object.
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return findByEmail(email);
    }
}