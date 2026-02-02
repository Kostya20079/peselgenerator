package com.peselgenerator.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPeselsToEmail(String toEmail, List<String> pesels) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Twoje wygenerowane numery PESEL");
            log.info("Send form: " + fromEmail);
            helper.setFrom(fromEmail);

            String body = buildEmailBody(pesels);
            helper.setText(body, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw e;
        }
    }

    private String buildEmailBody(List<String> pesels) {
        StringBuilder body = new StringBuilder();
        body.append("<html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif;'>");
        body.append("<h2 style='color: #3498db;'>Twoje wygenerowane numery PESEL</h2>");
        body.append("<p>Cześć!</p>");
        body.append("<p>Poniżej znajdują się Twoje wygenerowane numery PESEL:</p>");
        body.append("<div style='background-color: #ecf0f1; padding: 15px; border-radius: 5px;'>");
        body.append("<ol>");

        for (String pesel : pesels) {
            body.append("<li style='margin: 10px 0; font-family: monospace; font-size: 14px;'>");
            body.append("<strong>").append(pesel).append("</strong>");
            body.append("</li>");
        }

        body.append("</ol>");
        body.append("</div>");
        body.append("</body></html>");

        return body.toString();
    }
}