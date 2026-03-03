package com.parcelflow.infrastructure.extraction;

import com.parcelflow.domain.model.BarcodeType;
import com.parcelflow.domain.model.ParcelMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ChronopostDivertedExtractionAdapterTest {

    private final ChronopostDivertedExtractionAdapter adapter = new ChronopostDivertedExtractionAdapter();

    @Test
    void should_extract_metadata_from_diverted_email() throws Exception {
        // Arrange
        Path path = new ClassPathResource("emails/chronopost_diverted.html").getFile().toPath();
        String htmlContent = Files.readString(path);
        ZonedDateTime receivedAt = ZonedDateTime.of(2026, 2, 27, 10, 17, 0, 0, ZoneId.of("UTC"));

        // Act
        Optional<ParcelMetadata> result = adapter.extract(htmlContent, receivedAt);

        // Assert
        assertThat(result).isPresent();
        ParcelMetadata metadata = result.get();
        assertThat(metadata.trackingCode()).isEqualTo("XW313763680TS");
        assertThat(metadata.carrier()).isEqualTo("Vinted (Chronopost)");
        assertThat(metadata.expirationDate()).isEqualTo(LocalDate.of(2026, 3, 3));
        assertThat(metadata.pickupLocation()).isEqualTo("Consigne Otarie L Arbresle");
        assertThat(metadata.pickupCode()).isEqualTo("6315 / 8994");
        assertThat(metadata.qrCodeUrl()).isEqualTo("https://avisageng-colis-webexternal.pickup-services.com/api/barcode/DataMatrix?d=FR82478;XW313763680TS|63158994");
        assertThat(metadata.barcodeType()).isEqualTo(BarcodeType.DATA_MATRIX);
    }
}
