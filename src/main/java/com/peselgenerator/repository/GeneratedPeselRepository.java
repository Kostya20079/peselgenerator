package com.peselgenerator.repository;

import com.peselgenerator.entity.GeneratedPesel;
import com.peselgenerator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneratedPeselRepository extends JpaRepository<GeneratedPesel, Long> {
    List<GeneratedPesel> findByUserOrderByGeneratedAtDesc(User user);
    boolean existsByPeselNumber(String peselNumber);
}