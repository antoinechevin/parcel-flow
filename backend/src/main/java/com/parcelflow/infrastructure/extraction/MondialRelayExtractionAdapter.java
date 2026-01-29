package com.parcelflow.infrastructure.extraction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parcelflow.domain.model.ParcelMetadata;
import com.parcelflow.domain.ports.ParcelExtractionPort;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_TRAILING_COMMA.mappedFeature(), true);

    // Placeholder regexes to be fixed via sed
    private static final Pattern TRACKING_PATTERN = Pattern.compile("Votre colis\\s+(\\d+)\\s+est disponible", Pattern.CASE_INSENSITIVE);
    private static final Pattern TRACKING_BODY_PATTERN = Pattern.compile("Votre colis.*?<strong>(\\d+)</strong>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern LOCATION_LOCKER_PATTERN = Pattern.compile("Locker\\s+<span[^>]*?>\\s*(.*?)\\s*\\.", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern LOCATION_RELAIS_PATTERN = Pattern.compile("Point Relais.*?<span[^>]*?>\\s*(.*?)\\s*</span>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
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
            String pickupLocation = extractPickupLocation(doc, html);

            // 3. Extraction Date Limite
            LocalDate expirationDate = extractExpirationDate(text, receivedAt);

            // 4. Transporteur
            String carrier = "Mondial Relay";

            log.info("Mondial Relay extraction success: {} at {}", trackingNumber, pickupLocation);
            return Optional.of(new ParcelMetadata(trackingNumber, carrier, expirationDate, pickupLocation));

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

    private String extractPickupLocation(Document doc, String html) {
        // Try JSON-LD first
        Optional<String> jsonLdLocation = extractFromJsonLd(doc);
        if (jsonLdLocation.isPresent()) {
            return jsonLdLocation.get();
        }

        // Fallback to Regex
        Matcher matcher = LOCATION_LOCKER_PATTERN.matcher(html);
        if (matcher.find()) {
            return cleanLocationName(Jsoup.parse(matcher.group(1)).text());
        }
        matcher = LOCATION_RELAIS_PATTERN.matcher(html);
        if (matcher.find()) {
            return cleanLocationName(Jsoup.parse(matcher.group(1)).text());
        }
        return DEFAULT_PICKUP_LOCATION;
    }

    private Optional<String> extractFromJsonLd(Document doc) {
        try {
            for (Element script : doc.select("script[type=application/ld+json]")) {
                String json = script.data();
                JsonNode root = objectMapper.readTree(json);
                
                // Mondial Relay sometimes puts a space before @type
                String type = getFlexiblePath(root, "@type").asText();
                if ("ParcelDelivery".equals(type)) {
                    JsonNode deliveryAddress = getFlexiblePath(root, "deliveryAddress");
                    if (!deliveryAddress.isMissingNode()) {
                        String name = getFlexiblePath(deliveryAddress, "name").asText();
                        if (name != null && !name.isBlank()) {
                            return Optional.of(cleanLocationName(name));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Failed to parse optional JSON-LD from Mondial Relay email", e);
        }
        return Optional.empty();
    }

    private JsonNode getFlexiblePath(JsonNode node, String fieldName) {
        JsonNode exact = node.path(fieldName);
        if (!exact.isMissingNode()) return exact;
        return node.path(" " + fieldName);
    }

    private String cleanLocationName(String name) {
        if (name == null) return null;
        String cleaned = name.replaceAll("\\s+", " ").trim();
        // Remove trailing dots and whitespaces recursively
        while (cleaned.endsWith(".") || cleaned.endsWith(" ")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1).trim();
        }
        return cleaned;
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
