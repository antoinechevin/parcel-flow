package com.parcelflow.infrastructure.extraction;

import com.parcelflow.domain.model.ParcelMetadata;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class ChronopostPickupDivertedOverlapEmlTest {

    @Test
    void should_ignore_diverted_email_from_eml() throws Exception {
        InputStream is = new ClassPathResource("preview-emails/mail_chronopost_diverted.eml").getInputStream();
        MimeMessage msg = new MimeMessage(Session.getDefaultInstance(new Properties()), is);

        String emailContent = null;
        Object content = msg.getContent();
        if (content instanceof jakarta.mail.internet.MimeMultipart) {
            jakarta.mail.internet.MimeMultipart multipart = (jakarta.mail.internet.MimeMultipart) content;
            emailContent = multipart.getBodyPart(0).getContent().toString();
        } else {
            emailContent = content.toString();
        }

        System.out.println("Contains 'pas pu être livré dans votre point initial': " + emailContent.contains("pas pu être livré dans votre point initial"));
        System.out.println("Contains 'pas pu': " + emailContent.contains("pas pu"));
        System.out.println("Contains 'point initial': " + emailContent.contains("point initial"));

        ChronopostPickupExtractionAdapter adapter = new ChronopostPickupExtractionAdapter();
        Optional<ParcelMetadata> result = adapter.extract(emailContent, ZonedDateTime.now());

        assertThat(result).isEmpty();
    }
}
