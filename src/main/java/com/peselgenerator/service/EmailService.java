package com.peselgenerator.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Service responsible for email operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Sends an email containing the generated PESEL numbers as a text file attachment.
     *
     * @param toEmail the recipient's email address.
     * @param pesels  the list of generated PESEL numbers.
     * @throws MessagingException if creating or sending the message fails.
     */
    public void sendPeselsToEmail(String toEmail, List<String> pesels) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Twoje wygenerowane numery PESEL");
            helper.setFrom(fromEmail);

            String body = buildEmailBody(pesels.size());
            helper.setText(body, true);

            // preparing .txt
            String fileContent = String.join("\n", pesels);
            byte[] fileBytes = fileContent.getBytes("UTF-8");

            // adding file
            helper.addAttachment("pesels_" + System.currentTimeMillis() + ".txt",
                    () -> new java.io.ByteArrayInputStream(fileBytes),
                    "text/plain");

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw e;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Builds the HTML body for the email.
     *
     * @param peselCount the number of PESELs generated.
     * @return HTML string.
     */
    private String buildEmailBody(int peselCount) {
        return """
                <html>
                <head>
                    <meta charset='UTF-8'>
                    <style>
                        body { font-family: Arial, sans-serif; color: #2c3e50; }
                        .container { max-width: 600px; margin: 0 auto; }
                        .header { background-color: #3498db; color: white; padding: 20px; border-radius: 5px; text-align: center; }
                        .content { padding: 20px; background-color: #ecf0f1; border-radius: 5px; margin-top: 20px; }
                        .file-info { background-color: #2ecc71; color: white; padding: 15px; border-radius: 5px; margin-top: 15px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>Generator Numerów PESEL</h2>
                        </div>
                        <div class="content">
                            <p>Cześć!</p>
                            <p>Poniżej znajdują się Twoje wygenerowane numery PESEL (<strong>%d liczb</strong>).</p>
                            <div class="file-info">
                                <p><strong>Plik załączony:</strong> pesels_TIMESTAMP.txt</p>
                                <p>Wszystkie numery PESEL znajdują się w dołączonym pliku tekstowym.</p>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(peselCount);
    }
}