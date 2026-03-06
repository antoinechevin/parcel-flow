package com.parcelflow.infrastructure.extraction;

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

@Component
public class ChronopostDivertedExtractionAdapter implements ParcelExtractionPort {

    private static final Logger log = LoggerFactory.getLogger(ChronopostDivertedExtractionAdapter.class);

    private static final Pattern TRACKING_PATTERN = Pattern.compile("n°\\s*(XW\\d{9}TS|[A-Z]{2}\\d{9}[A-Z]{2})", Pattern.CASE_INSENSITIVE);
    private static final Pattern DATE_PATTERN = Pattern.compile("jusqu'au\\s+(?:<strong>)?(.*?)(?:</strong>)?\\.", Pattern.CASE_INSENSITIVE);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);

    @Override
    public Optional<ParcelMetadata> extract(String emailContent, java.time.ZonedDateTime receivedAt) {
        // Specificity: the diverted email contains "pas pu être livré dans votre point initial" or similar text
        if (!emailContent.contains("pas pu") && !emailContent.contains("livré dans votre point initial") && !emailContent.contains("Malheureusement, votre colis n’a pas pu être livré")) {
            return Optional.empty();
        }

        try {
            Document doc = Jsoup.parse(emailContent);
            String text = doc.text();

            // 1. Extraction Tracking Number
            String trackingNumber = extractTrackingNumber(doc, text);
            if (trackingNumber == null) return Optional.empty();

            // 2. Extraction Date Limite
            LocalDate expirationDate = extractExpirationDate(text);

            // 3. Extraction Lieu
            String pickupLocation = extractPickupLocation(doc, text);

            // 4. Extraction Code PIN et QR Code
            String qrCodeUrl = extractQrCodeUrl(doc);
            String pickupCode = extractPickupCode(doc, text);

            // 5. Transporteur
            String carrier = "Chronopost / Pickup";
            if (emailContent.toUpperCase().contains("VINTED")) {
                carrier = "Vinted (Chronopost)";
            }

            BarcodeType barcodeType = qrCodeUrl != null ? BarcodeType.DATA_MATRIX : BarcodeType.NONE;

            log.info("Chronopost Diverted extraction success: {} at {}", trackingNumber, pickupLocation);
            return Optional.of(new ParcelMetadata(trackingNumber, carrier, expirationDate, pickupLocation, pickupCode, qrCodeUrl, barcodeType));

        } catch (Exception e) {
            log.warn("Failed to parse Chronopost diverted email", e);
            return Optional.empty();
        }
    }

    private String extractTrackingNumber(Document doc, String text) {
        Matcher matcher = TRACKING_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private LocalDate extractExpirationDate(String text) {
        Matcher matcher = DATE_PATTERN.matcher(text);
        if (matcher.find()) {
            String dateStr = matcher.group(1).trim().toLowerCase();
            try {
                // Remove HTML tags just in case
                dateStr = Jsoup.parse(dateStr).text();
                return LocalDate.parse(dateStr, DATE_FORMATTER);
            } catch (Exception e) {
                log.warn("Date parse error for '{}': {}", dateStr, e.getMessage());
            }
        }
        return null;
    }

    private String extractPickupLocation(Document doc, String text) {
        // Try looking for the exact Consigne name in the strong tag if available
        // First try to find "Votre consigne Pickup" section in HTML
        org.jsoup.select.Elements blocks = doc.select("td:contains(Votre consigne Pickup)");
        if (!blocks.isEmpty()) {
            // Find the first strong element after this block or within the next blocks
            org.jsoup.select.Elements strongs = doc.select("strong");
            for (Element strong : strongs) {
                if (strong.text().contains("Consigne") || strong.text().contains("Relais")) {
                    return strong.text().trim();
                }
            }
        }

        // Fallback regex on HTML since text might merge things unexpectedly
        String html = doc.html();
        Pattern htmlPattern = Pattern.compile("disponible (?:en|au|à)\\s+(.*?)\\s+jusqu'au", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher htmlMatcher = htmlPattern.matcher(html);
        if (htmlMatcher.find()) {
             return Jsoup.parse(htmlMatcher.group(1)).text().trim();
        }

        Pattern p = Pattern.compile("disponible (?:en|au|à)\\s+(.*?)\\s+jusqu'au", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        if (m.find()) {
            String extracted = m.group(1).trim();
            // Since `text` strips HTML, it might include previous tags' texts. Let's clean it.
            if (extracted.contains("est disponible en ")) {
                extracted = extracted.substring(extracted.lastIndexOf("est disponible en ") + 18);
            } else if (extracted.contains("est disponible au ")) {
                extracted = extracted.substring(extracted.lastIndexOf("est disponible au ") + 18);
            } else if (extracted.contains("est disponible à ")) {
                extracted = extracted.substring(extracted.lastIndexOf("est disponible à ") + 17);
            }
            return extracted;
        }

        return null;
    }

    private String extractQrCodeUrl(Document doc) {
        Element img = doc.select("img[src*=barcode/DataMatrix]").first();
        if (img == null) {
            img = doc.select("img[src*=barcode]").first();
        }
        return img != null ? img.attr("src") : null;
    }

    private String extractPickupCode(Document doc, String text) {
        // Look for Identifiant and Code d'ouverture in Locker emails
        Pattern p = Pattern.compile("Identifiant\\s*:\\s*(\\d+).*?Code d'ouverture\\s*:\\s*(\\d+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(1) + " / " + m.group(2); // Provide both id and code
        }

        // If simple pickup code
        Pattern pinPattern = Pattern.compile("code de retrait\\s*:\\s*(\\d{4,8})", Pattern.CASE_INSENSITIVE);
        Matcher pinMatcher = pinPattern.matcher(text);
        if (pinMatcher.find()) {
            return pinMatcher.group(1);
        }

        return null;
    }
}
