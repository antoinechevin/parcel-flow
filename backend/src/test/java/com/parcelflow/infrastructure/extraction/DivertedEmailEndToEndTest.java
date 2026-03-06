package com.parcelflow.infrastructure.extraction;

import com.parcelflow.application.usecases.ExtractParcelUseCase;
import com.parcelflow.domain.model.BarcodeType;
import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelStatus;
import com.parcelflow.domain.model.PickupPoint;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class DivertedEmailEndToEndTest {

    @Autowired
    private ParcelRepositoryPort repository;

    @Autowired
    private ExtractParcelUseCase extractParcelUseCase;

    @Autowired
    private ChronopostDivertedExtractionAdapter divertedAdapter;

    @Test
    void should_update_existing_bad_location_with_new_good_location() throws Exception {
        // 1. Insert bad data (simulating what happened in prod)
        String trackingNumber = "XW313763680TS";
        PickupPoint badPoint = new PickupPoint("bad-id", "Point Relais (Adresse non détectée)", "Point Relais (Adresse non détectée)", null);
        Parcel badParcel = new Parcel(
            ParcelId.random(),
            trackingNumber,
            "Chronopost / Pickup",
            LocalDate.now().plusDays(5),
            ParcelStatus.AVAILABLE,
            badPoint,
            "1234",
            null,
            BarcodeType.NONE
        );
        repository.save(badParcel);

        // Verify it was saved correctly
        Parcel savedBad = repository.findByTrackingNumber(trackingNumber).orElseThrow();
        assertThat(savedBad.pickupPoint().name()).isEqualTo("Point Relais (Adresse non détectée)");

        // 2. Fetch the diverted email mock
        Path path = new ClassPathResource("preview-emails/mail_chronopost_diverted.eml").getFile().toPath();
        String emlContent = Files.readString(path);

        // Simulating the email body extraction:
        jakarta.mail.internet.MimeMessage msg = new jakarta.mail.internet.MimeMessage(jakarta.mail.Session.getDefaultInstance(new java.util.Properties()), new java.io.ByteArrayInputStream(emlContent.getBytes()));
        String body = msg.getContent() instanceof jakarta.mail.internet.MimeMultipart
            ? ((jakarta.mail.internet.MimeMultipart) msg.getContent()).getBodyPart(0).getContent().toString()
            : msg.getContent().toString();

        // 3. Process the email with the specific adapter
        extractParcelUseCase.execute(body, ZonedDateTime.now(), divertedAdapter);

        // 4. Check if the location was successfully overwritten
        Parcel updated = repository.findByTrackingNumber(trackingNumber).orElseThrow();
        System.out.println("UPDATED LOCATION NAME: " + updated.pickupPoint().name());

        assertThat(updated.pickupPoint().name()).isEqualTo("Consigne Otarie L Arbresle");
    }
}
