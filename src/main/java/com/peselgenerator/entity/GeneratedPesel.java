package com.peselgenerator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a generated PESEL number stored in the database.
 * Maps to the 'generated_pesels' table.
 */
@Entity
@Table(name = "generated_pesels", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_generated_at", columnList = "generated_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratedPesel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who generated this PESEL. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** The actual 11-digit PESEL number. */
    @Column(nullable = false, length = 11, unique = true)
    private String peselNumber;

    /** Timestamp of when the PESEL was generated. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime generatedAt;

    /**
     * Sets the timestamp before persisting to the database.
     */
    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
}