package com.peselgenerator.service;

import com.peselgenerator.repository.GeneratedPeselRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@Slf4j
@Service
@AllArgsConstructor
public class PeselService {

    private final GeneratedPeselRepository generatedPeselRepository;

    public String generateSinglePesel(String birthDateStr, int gender) {
        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            return generatePesel(birthDate, gender);
        } catch (Exception e) {
            throw new RuntimeException("Error while generating PESEL: " + e.getMessage());
        }
    }

    /**
     * PESEL: 11 digits in format: YYMMDDXXXGC
     * - YYMMDD: date of birth
     * - XXX: serial number
     * - G: gender
     * - C: checksum
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
     * Calculates the PESEL checksum
     * scales: 1, 3, 7, 9, 1, 3, 7, 9, 1, 3
     *
     * 1. Multiply each digit by its corresponding weight
     * 2. Sum up all the products
     * 3. Divide the sum by 10 and take the remainder
     * 4. Subtract from 10 this is the check digit
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