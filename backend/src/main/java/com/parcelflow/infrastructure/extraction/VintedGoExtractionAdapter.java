package com.parcelflow.infrastructure.extraction;

import com.parcelflow.domain.model.BarcodeType;
import com.parcelflow.domain.model.ParcelMetadata;
import com.parcelflow.domain.ports.ParcelExtractionPort;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class VintedGoExtractionAdapter implements ParcelExtractionPort {

    private static final Logger log = LoggerFactory.getLogger(VintedGoExtractionAdapter.class);

    private static final String SUBJECT_TRIGGER = "Il est temps de récupérer ton colis";
    private static final Pattern CODE_PATTERN = Pattern.compile("saisis le code suivant\\s*:\\s*<b>([A-Z0-9]+)</b>", Pattern.CASE_INSENSITIVE);
    private static final Pattern DEADLINE_TEXT_PATTERN = Pattern.compile("retirer avant le\\s*(\\d{2}/\\d{2}/\\d{4})", Pattern.CASE_INSENSITIVE);
    private static final Pattern TRACKING_PATTERN = Pattern.compile("Numéro de suivi\\s*:\\s*<a[^>]*>(\\d+)</a>", Pattern.CASE_INSENSITIVE);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public Optional<ParcelMetadata> extract(String emailContent, ZonedDateTime receivedAt) {
        if (emailContent == null) return Optional.empty();
        
        boolean isVinted = emailContent.contains(SUBJECT_TRIGGER) || 
                           emailContent.contains("Vinted Go") || 
                           emailContent.contains("vintedgo.com");

        if (!isVinted) {
             return Optional.empty();
        }

        try {
            // 1. Extraction Code
            String pickupCode = extractPickupCode(emailContent);
            if (pickupCode == null) return Optional.empty(); // Essential

            Document doc = Jsoup.parse(emailContent);
            String text = doc.text(); // Clean text

            // 2. Extraction Date
            LocalDate deadline = extractDeadline(emailContent, text);

            // 3. Extraction Tracking
            String trackingNumber = extractTracking(emailContent);
            
            // 4. Extraction Lieu
            String location = extractLocation(doc);

            // 5. Extraction QR Code URL
            String qrCodeUrl = extractQrCodeUrl(doc);
            BarcodeType barcodeType = qrCodeUrl != null ? BarcodeType.QR_CODE : BarcodeType.NONE;

            return Optional.of(new ParcelMetadata(
                trackingNumber != null ? trackingNumber : "UNKNOWN",
                "Vinted Go",
                deadline,
                location,
                pickupCode,
                qrCodeUrl,
                barcodeType
            ));

        } catch (Exception e) {
            log.warn("Failed to extract Vinted Go email", e);
            return Optional.empty();
        }
    }

    private String extractQrCodeUrl(Document doc) {
        Element img = doc.select("img[alt*=QR]").first();
        if (img == null) {
            img = doc.select("img[src*=qr_codes]").first();
        }
        return img != null ? img.attr("src") : null;
    }

    private String extractPickupCode(String html) {
        Matcher m = CODE_PATTERN.matcher(html);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private LocalDate extractDeadline(String html, String text) {
        // Try text first
        Matcher m = DEADLINE_TEXT_PATTERN.matcher(text);
        if (m.find()) {
            try {
                return LocalDate.parse(m.group(1), DATE_FORMATTER);
            } catch (Exception e) {
                log.warn("Date parse error", e);
            }
        }
        // Try regex on HTML if text failed
        Pattern htmlPattern = Pattern.compile("retirer avant le.*?<b>(\\d{2}/\\d{2}/\\d{4})</b>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        m = htmlPattern.matcher(html);
        if (m.find()) {
             try {
                return LocalDate.parse(m.group(1), DATE_FORMATTER);
            } catch (Exception e) {
                log.warn("Date parse error (HTML)", e);
            }
        }
        return null;
    }

    private String extractTracking(String html) {
        Matcher m = TRACKING_PATTERN.matcher(html);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private String extractLocation(Document doc) {
        try {
            Elements elements = doc.getElementsContainingOwnText("Adresse");
            for (Element el : elements) {
                if (el.className().contains("block-header")) {
                    Element container = el.parent();
                    if (container != null) {
                        String fullText = container.text();
                        return fullText.replace("Adresse", "").trim();
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Location extraction failed", e);
        }
        return "Point de retrait Vinted Go";
    }
}
