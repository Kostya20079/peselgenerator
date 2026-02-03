package com.peselgenerator.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for carrying PESEL generation request data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratePeselRequest {

    /** The birth date for the PESEL generation in YYYY-MM-DD format. */
    @NotBlank(message = "Birth date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in YYYY-MM-DD format")
    private String birthDate;

    /** The gender for the PESEL (0 for female, 1 for male). */
    @NotNull(message = "Gender is required")
    @Min(value = 0, message = "Gender must be 0 (female) or 1 (male)")
    @Max(value = 1, message = "Gender must be 0 (female) or 1 (male)")
    private Integer gender;

    /** The number of PESELs to generate (default is 1). */
    @Min(value = 1, message = "Number of entries must be at least 1")
    @Max(value = 200, message = "Number of entries must not exceed 200")
    private Integer count = 1;

}