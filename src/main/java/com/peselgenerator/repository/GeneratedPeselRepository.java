package com.peselgenerator.repository;

import com.peselgenerator.entity.GeneratedPesel;
import com.peselgenerator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for accessing {@link GeneratedPesel} data.
 */
@Repository
public interface GeneratedPeselRepository extends JpaRepository<GeneratedPesel, Long> {

    /**
     * Finds all generated PESELs for a specific user, ordered by generation date (descending).
     * @param user the user entity.
     * @return list of generated PESELs.
     */
    List<GeneratedPesel> findByUserOrderByGeneratedAtDesc(User user);

    /**
     * Checks if a PESEL number already exists in the database.
     * @param peselNumber the PESEL string.
     * @return true if exists, false otherwise.
     */
    boolean existsByPeselNumber(String peselNumber);
}