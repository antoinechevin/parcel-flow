package com.parcelflow.infrastructure.extraction;

import com.parcelflow.domain.model.ParcelMetadata;
import com.parcelflow.domain.ports.ParcelExtractionPort;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MondialRelayExtractionAdapter implements ParcelExtractionPort {

    private static final Logger log = LoggerFactory.getLogger(MondialRelayExtractionAdapter.class);
    private static final String DEFAULT_PICKUP_LOCATION = "Mondial Relay Point";
    private static final java.time.ZoneId PARIS_ZONE = java.time.ZoneId.of("Europe/Paris");

    // Placeholder regexes to be fixed via sed
    private static final Pattern TRACKING_PATTERN = Pattern.compile("Votre colis\\s+(\\d+)\\s+est disponible", Pattern.CASE_INSENSITIVE);
    private static final Pattern TRACKING_BODY_PATTERN = Pattern.compile("Votre colis.*?<strong>(\\d+)</strong>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern PIN_PATTERN = Pattern.compile("<div[^>]*?>(\\d{6})</div>", Pattern.CASE_INSENSITIVE);
    private static final Pattern LOCATION_PATTERN = Pattern.compile("Locker\\s+<span[^>]*?>\\s*(.*?)\\s*\\.", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern DEADLINE_PATTERN = Pattern.compile("DANS\\s+(\\d+)\\s+JOURS\\s+VOTRE\\s+COLIS\\s+REPARTIRA", Pattern.CASE_INSENSITIVE);

    @Override
    public Optional<ParcelMetadata> extract(String emailContent, ZonedDateTime receivedAt) {
        if (!emailContent.contains("Mondial Relay")) {
            return Optional.empty();
        }

        try {
            Document doc = Jsoup.parse(emailContent);
            String text = doc.text();
            String html = doc.html();

            // 1. Extraction Tracking Number
            String trackingNumber = extractTrackingNumber(text, html);
            if (trackingNumber == null) return Optional.empty();

            // 2. Extraction Lieu
            String pickupLocation = extractPickupLocation(html);

            // 3. Extraction Date Limite
            LocalDate expirationDate = extractExpirationDate(text, receivedAt);

            // 4. Transporteur
            String carrier = "Mondial Relay";

            log.info("Mondial Relay extraction success: {} at {}", trackingNumber, pickupLocation);
            return Optional.of(new ParcelMetadata(trackingNumber, null, carrier, expirationDate, pickupLocation));

        } catch (Exception e) {
            log.warn("Failed to parse Mondial Relay email", e);
            return Optional.empty();
        }
    }

    private String extractTrackingNumber(String text, String html) {
        Matcher matcher = TRACKING_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        matcher = TRACKING_BODY_PATTERN.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractPickupLocation(String html) {
        Matcher matcher = LOCATION_PATTERN.matcher(html);
        if (matcher.find()) {
            return Jsoup.parse(matcher.group(1)).text().trim();
        }
        return DEFAULT_PICKUP_LOCATION;
    }

    private LocalDate extractExpirationDate(String text, ZonedDateTime receivedAt) {
        Matcher matcher = DEADLINE_PATTERN.matcher(text);
        if (matcher.find()) {
            try {
                long days = Long.parseLong(matcher.group(1));
                if (days < 0 || days > 365) {
                    log.warn("Extracted suspicious number of days: {}", days);
                    return null;
                }
                // Convert receivedAt to Paris time for accurate daily calculation
                return receivedAt.withZoneSameInstant(PARIS_ZONE).toLocalDate().plusDays(days);
            } catch (NumberFormatException e) {
                log.warn("Failed to parse days from string: {}", matcher.group(1));
                return null;
            }
        }
        return null;
    }
}
