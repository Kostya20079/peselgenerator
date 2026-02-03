package com.peselgenerator.service;

import com.peselgenerator.entity.GeneratedPesel;
import com.peselgenerator.entity.User;
import com.peselgenerator.repository.GeneratedPeselRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Service responsible for the logic of generating and validating PESEL numbers.
 *
 */
@Slf4j
@Service
@AllArgsConstructor
public class PeselService {

    private final GeneratedPeselRepository generatedPeselRepository;

    /**
     * Generates a single PESEL number.
     *
     * @param birthDateStr the birth date in ISO format (YYYY-MM-DD).
     * @param gender       the gender code (0 for female, 1 for male).
     * @return a valid 11-digit PESEL string.
     * @throws RuntimeException if the date format is invalid or generation fails.
     */
    public String generateSinglePesel(String birthDateStr, int gender) {
        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            return generatePesel(birthDate, gender);
        } catch (Exception e) {
            throw new RuntimeException("Error while generating PESEL: " + e.getMessage());
        }
    }

    /**
     * Generates multiple unique PESEL numbers.
     * Ensures uniqueness within the generated batch.
     *
     * @param birthDateStr the birth date in ISO format.
     * @param gender       the gender code.
     * @param count        the number of PESELs to generate.
     * @return a list of unique PESEL strings.
     * @throws RuntimeException if generation fails.
     */
    public List<String> generateMultiplePesel(String birthDateStr, int gender, int count) {
        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            List<String> pesels = new ArrayList<>();
            // for unique pesel numbers
            Set<String> uniquePesels = new HashSet<>();

            int maxAttempts = count * 10;
            int attempts = 0;

            while (uniquePesels.size() < count && attempts < maxAttempts) {
                String pesel = generatePesel(birthDate, gender);
                uniquePesels.add(pesel);
                attempts++;
            }

            pesels.addAll(uniquePesels);
            return pesels;
        } catch (Exception e) {
            throw new RuntimeException("Error while generating many PESELs: " + e.getMessage());
        }
    }

    /**
     * Persists a list of generated PESELs to the database for a specific user.
     * Checks if the PESEL already exists globally in the DB before saving.
     *
     * @param user   the user who generated the numbers.
     * @param pesels the list of PESEL strings.
     */
    @Transactional
    public void savePesels(User user, List<String> pesels) {
        try {
            for (String pesel : pesels) {
                // check if pesel is already exists in db
                if (!generatedPeselRepository.existsByPeselNumber(pesel)) {
                    GeneratedPesel generatedPesel = GeneratedPesel.builder()
                            .user(user)
                            .peselNumber(pesel)
                            .build();
                    generatedPeselRepository.save(generatedPesel);
                }
            }
            log.info("{} PESEL numbers saved for the user: {}", pesels.size(), user.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Error while writing PESEL: " + e.getMessage());
        }
    }

    /**
     * Internal method to construct a PESEL string.
     * <p>
     * PESEL format: YYMMDDXXXGC
     * <ul>
     * <li>YYMMDD: date of birth (Year, Month, Day)</li>
     * <li>XXX: serial number (random)</li>
     * <li>G: gender digit (even for female, odd for male)</li>
     * <li>C: checksum</li>
     * </ul>
     *
     * @param birthDate date of birth.
     * @param gender    0 (female) or 1 (male).
     * @return constructed PESEL string.
     */
    private String generatePesel(LocalDate birthDate, int gender) {
        StringBuilder pesel = new StringBuilder();
        Random random = new Random();

        // date of birth
        String year = String.format("%02d", birthDate.getYear() % 100);
        String month = String.format("%02d", birthDate.getMonthValue());
        String day = String.format("%02d", birthDate.getDayOfMonth());
        pesel.append(year).append(month).append(day);

        // serial number
        for (int i = 0; i < 3; i++) {
            pesel.append(random.nextInt(10));
        }

        // gender
        int genderDigit;
        if (gender == 1) {
            // male
            genderDigit = random.nextInt(5) * 2 + 1;
        } else {
            // female
            genderDigit = random.nextInt(5) * 2;
        }
        pesel.append(genderDigit);

        // checksum
        String peselWithoutChecksum = pesel.toString();
        int checksum = calculateChecksum(peselWithoutChecksum);
        pesel.append(checksum);

        return pesel.toString();
    }


    /**
     * Calculates the PESEL checksum digit.
     * <p>
     * Algorithm:
     * 1. Multiply each of the first 10 digits by corresponding weight: 1, 3, 7, 9, 1, 3, 7, 9, 1, 3.
     * 2. Sum the products.
     * 3. M = sum % 10.
     * 4. Checksum = (10 - M) % 10.
     *
     * @param peselWithoutChecksum the first 10 digits of the PESEL.
     * @return the calculated checksum digit.
     */
    private int calculateChecksum(String peselWithoutChecksum) {
        int[] weights = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};
        int sum = 0;

        for (int i = 0; i < 10; i++) {
            int digit = Character.getNumericValue(peselWithoutChecksum.charAt(i));
            sum += digit * weights[i];
        }

        return (10 - (sum % 10)) % 10;
    }
}