package com.parcelflow.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class EmailMetadataTest {

    @Test
    void shouldCreateEmailMetadata() {
        String id = "123";
        String subject = "Colis en route";
        String snippet = "Votre colis arrive...";
        LocalDateTime date = LocalDateTime.now();

        EmailMetadata email = new EmailMetadata(id, subject, snippet, date);

        assertThat(email.id()).isEqualTo(id);
        assertThat(email.subject()).isEqualTo(subject);
        assertThat(email.snippet()).isEqualTo(snippet);
        assertThat(email.receivedAt()).isEqualTo(date);
    }
}
