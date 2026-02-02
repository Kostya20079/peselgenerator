package com.peselgenerator.controller;

import com.peselgenerator.dto.GeneratePeselRequest;
import com.peselgenerator.entity.User;
import com.peselgenerator.service.EmailService;
import com.peselgenerator.service.PeselService;
import com.peselgenerator.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@AllArgsConstructor
public class PeselController {

    private final PeselService peselService;
    private final UserService userService;
    private final EmailService emailService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        model.addAttribute("user", user);
        model.addAttribute("generateRequest", new GeneratePeselRequest());
        return "dashboard";
    }

    @PostMapping("/generate-multiple")
    public String generateMultiple(
            @Valid GeneratePeselRequest request,
            BindingResult result,
            Authentication authentication,
            Model model) {

        log.info("Generating multiple PESELs");

        String email = authentication.getName();
        User user = userService.findByEmail(email);
        model.addAttribute("user", user);
        model.addAttribute("generateRequest", new GeneratePeselRequest());

        // check validation
        if (result.hasErrors()) {
            return "dashboard";
        }

        try {
            // generating pesels
            List<String> pesels = peselService.generateMultiplePesel(
                    request.getBirthDate(),
                    request.getGender(),
                    request.getCount()
            );

            // writing to db
            peselService.savePesels(user, pesels);

            model.addAttribute("pesels", pesels);
            model.addAttribute("success", "Generated " + pesels.size() + " PESEL numbers!");

        } catch (Exception e) {
            log.error("Error generating PESELs: ", e);
            model.addAttribute("error", "Error: " + e.getMessage());
        }

        return "dashboard";
    }

    @PostMapping("/download-pesel")
    public ResponseEntity<byte[]> downloadPesel(
            @Valid GeneratePeselRequest request,
            BindingResult result,
            Authentication authentication) {

        // check validation
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<String> pesels = peselService.generateMultiplePesel(
                    request.getBirthDate(),
                    request.getGender(),
                    request.getCount()
            );

            String email = authentication.getName();
            User user = userService.findByEmail(email);
            peselService.savePesels(user, pesels);

            // preparing the file contents
            String content = String.join("\n", pesels);
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

            // HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentLength(bytes.length);
            headers.set("Content-Disposition", "attachment; filename=\"pesels_" + System.currentTimeMillis() + ".txt\"");

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/send-email")
    public String sendEmail(
            @Valid GeneratePeselRequest request,
            BindingResult result,
            Authentication authentication,
            Model model) {

        // check validation
        if (result.hasErrors()) {
            model.addAttribute("generateRequest", new GeneratePeselRequest());
            return "dashboard";
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email);
        model.addAttribute("user", user);
        model.addAttribute("generateRequest", new GeneratePeselRequest());


        try {
            List<String> pesels = peselService.generateMultiplePesel(
                    request.getBirthDate(),
                    request.getGender(),
                    request.getCount()
            );

            peselService.savePesels(user, pesels);

            // sending mail
            emailService.sendPeselsToEmail(email, pesels);

            model.addAttribute("success", "Numery PESEL zostały wysłane na Twój email!");

        } catch (MessagingException e) {
            model.addAttribute("error", "Error while sending email!");
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
        }

        model.addAttribute("generateRequest", new GeneratePeselRequest());
        return "dashboard";
    }
}