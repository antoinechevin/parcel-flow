package com.parcelflow.infrastructure.adapters.extraction.chronopost;

import com.parcelflow.domain.model.BarcodeType;
import com.parcelflow.domain.model.ParcelMetadata;
import com.parcelflow.domain.ports.ParcelExtractionPort;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Strategy specifically designed for Chronopost rerouting emails ("Diversion").
 * This strategy identifies emails where the parcel was redirected to a new relay point.
 */
@Component
public class ChronopostReroutingStrategy implements ParcelExtractionPort {

    private static final Logger log = LoggerFactory.getLogger(ChronopostReroutingStrategy.class);

    // Fingerprint to identify rerouting email
    private static final String REROUTING_FINGERPRINT = "n’a pas pu être livré dans votre point initial";

    private static final Pattern TRACKING_PATTERN = Pattern.compile("XW\\d{9}TS", Pattern.CASE_INSENSITIVE);
    private static final Pattern EXPIRATION_DATE_PATTERN = Pattern.compile("jusqu'au\\s+<strong>(.*?)</strong>", Pattern.CASE_INSENSITIVE);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);

    // Specific regex for new relay point name in this format
    // Matches: "est disponible en [Point Name] jusqu'au"
    // Using a more restrictive pattern to avoid capturing the whole preceding text
    private static final Pattern RELAY_POINT_PATTERN = Pattern.compile("est disponible en\\s+([^\\.!\\?]+?)(?=\\s+jusqu'au|$)", Pattern.CASE_INSENSITIVE);

    @Override
    public Optional<ParcelMetadata> extract(String emailContent, java.time.ZonedDateTime receivedAt) {
        if (!emailContent.contains(REROUTING_FINGERPRINT)) {
            return Optional.empty();
        }

        try {
            Document doc = Jsoup.parse(emailContent);
            String text = doc.text();

            // 1. Extraction Tracking Number
            String trackingNumber = extractTrackingNumber(text);
            if (trackingNumber == null) return Optional.empty();

            // 2. Extraction Date Limite
            LocalDate expirationDate = extractExpirationDate(text, emailContent);

            // 3. Extraction Lieu (New Relay Point)
            String pickupLocation = extractPickupLocation(text);

            // 4. Extraction Code PIN et QR Code
            String qrCodeUrl = extractQrCodeUrl(doc);
            String pickupCode = extractPickupCode(doc, text);

            log.info("Chronopost rerouting extraction success: {} at {}", trackingNumber, pickupLocation);
            return Optional.of(new ParcelMetadata(trackingNumber, "Chronopost (Rerouté)", expirationDate, pickupLocation, pickupCode, qrCodeUrl, BarcodeType.AZTEC));

        } catch (Exception e) {
            log.warn("Failed to parse Chronopost rerouting email", e);
            return Optional.empty();
        }
    }

    private String extractTrackingNumber(String text) {
        Matcher matcher = TRACKING_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(0).trim();
        }
        return null;
    }

    private LocalDate extractExpirationDate(String text, String html) {
        // First try from HTML because of the <strong> tag
        Matcher htmlMatcher = EXPIRATION_DATE_PATTERN.matcher(html);
        if (htmlMatcher.find()) {
            String dateStr = Jsoup.parse(htmlMatcher.group(1)).text().trim().toLowerCase();
            try {
                return LocalDate.parse(dateStr, DATE_FORMATTER);
            } catch (Exception e) {
                log.warn("Date parse error for '{}': {}", dateStr, e.getMessage());
            }
        }
        return null;
    }

    private String extractPickupLocation(String text) {
        Matcher matcher = RELAY_POINT_PATTERN.matcher(text);
        if (matcher.find()) {
            String location = matcher.group(1).trim();
            // Clean "n° XXXXX " if present at start
            return location.replaceFirst("(?i)^n°\\s+[A-Z0-9]+\\s+", "").trim();
        }
        
        // Fallback: Look for "Votre consigne Pickup" or similar block
        if (text.contains("Votre consigne Pickup")) {
            // Find "Consigne ..." in text
            Pattern fallbackP = Pattern.compile("Consigne\\s+[^\\s]+\\s+[^\\s]+", Pattern.CASE_INSENSITIVE);
            Matcher fm = fallbackP.matcher(text);
            if (fm.find()) {
                return fm.group(0).trim();
            }
        }
        
        return "Point Relais (Rerouté)";
    }

    private String extractQrCodeUrl(Document doc) {
        Element img = doc.select("img[src*=barcode]").first();
        if (img == null) {
            img = doc.select("img[alt*=QR]").first();
        }
        return img != null ? img.attr("src") : null;
    }

    private String extractPickupCode(Document doc, String text) {
        // Look for "Identifiant : XXXX" and "Code d'ouverture : YYYY"
        Pattern idPattern = Pattern.compile("Identifiant\\s*:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Pattern codePattern = Pattern.compile("Code d'ouverture\\s*:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        
        Matcher idMatcher = idPattern.matcher(text);
        Matcher codeMatcher = codePattern.matcher(text);
        
        if (idMatcher.find() && codeMatcher.find()) {
            return idMatcher.group(1) + " " + codeMatcher.group(1);
        }
        
        if (codeMatcher.find()) {
            return codeMatcher.group(1);
        }

        return null;
    }
}
