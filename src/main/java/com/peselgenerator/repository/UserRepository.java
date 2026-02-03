package com.peselgenerator.repository;

import com.peselgenerator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing {@link User} data.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * @param email the email to search for.
     * @return an Optional containing the user if found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the given email exists.
     * @param email the email to check.
     * @return true if exists, false otherwise.
     */
    boolean existsByEmail(String email);
}