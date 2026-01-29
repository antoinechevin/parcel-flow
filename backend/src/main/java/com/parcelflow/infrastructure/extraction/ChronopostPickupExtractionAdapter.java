package com.parcelflow.infrastructure.extraction;

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
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ChronopostPickupExtractionAdapter implements ParcelExtractionPort {

    private static final Logger log = LoggerFactory.getLogger(ChronopostPickupExtractionAdapter.class);
    
    // Pattern pour la date : "mercredi 28 janvier 2026"
    private static final Pattern DATE_PATTERN = Pattern.compile("jusqu'au\s+(?:<strong>)?(.*?)(?:</strong>)?\\.", Pattern.CASE_INSENSITIVE);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);

    @Override
    public Optional<ParcelMetadata> extract(String emailContent, java.time.ZonedDateTime receivedAt) {
        if (!emailContent.contains("Chronopost") && !emailContent.contains("Pickup")) {
            return Optional.empty();
        }

        try {
            Document doc = Jsoup.parse(emailContent);
            
            // 1. Extraction Tracking Number
            // Cherche un lien qui ressemble à un tracking ou le texte "n° XW..."
            String trackingNumber = extractTrackingNumber(doc);
            if (trackingNumber == null) return Optional.empty();

            // 2. Extraction Date Limite
            LocalDate expirationDate = extractExpirationDate(doc);

            // 3. Extraction Lieu
            String pickupLocation = extractPickupLocation(doc);

            // 4. Transporteur (Fixe pour cet adapter)
            String carrier = "Chronopost / Pickup";
            if (emailContent.toUpperCase().contains("VINTED")) {
                carrier = "Vinted (Chronopost)";
            }

            log.info("Chronopost extraction success: {} at {}", trackingNumber, pickupLocation);
            return Optional.of(new ParcelMetadata(trackingNumber, carrier, expirationDate, pickupLocation));

        } catch (Exception e) {
            log.warn("Failed to parse Chronopost email", e);
            return Optional.empty();
        }
    }

    private String extractTrackingNumber(Document doc) {
        String text = doc.text();

        // Stratégie 1: Recherche par préfixe "n°" (très fréquent dans les mails Chronopost/Vinted)
        Pattern prefixPattern = Pattern.compile("n°\\s*([A-Z0-9]{10,20})\\b", Pattern.CASE_INSENSITIVE);
        Matcher prefixMatcher = prefixPattern.matcher(text);
        if (prefixMatcher.find()) {
            return prefixMatcher.group(1).trim();
        }

        // Stratégie 2: Fallback Regex sur pattern standard XW...TS ou pattern Pickup
        Pattern fallbackPattern = Pattern.compile("\\b(XW\\d{9}TS|[A-Z]{2}\\d{9}[A-Z]{2})\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = fallbackPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        return null;
    }

    private LocalDate extractExpirationDate(Document doc) {
        // Cherche le texte "jusqu'au ..."
        String text = doc.text();
        Matcher matcher = DATE_PATTERN.matcher(text);
        if (matcher.find()) {
            String dateStr = matcher.group(1).trim().toLowerCase();
            try {
                // Nettoyage préventif (enlever les balises HTML qui auraient pu passer)
                dateStr = Jsoup.parse(dateStr).text(); 
                return LocalDate.parse(dateStr, DATE_FORMATTER);
            } catch (Exception e) {
                log.warn("Date parse error for '{}': {}", dateStr, e.getMessage());
            }
        }
        return null;
    }

    private String extractPickupLocation(Document doc) {
        // Le mail contient souvent une structure : "Votre relais Pickup" suivi d'un tableau
        // Stratégie : Chercher le bloc qui contient l'adresse
        // Dans l'exemple: "ÉPICERIE DES MOINES 8 RUE DE L ÉGLISE 69210 EVEUX"
        
        // On cherche le nom du point qui est souvent en gras ou en majuscules dans une table spécifique
        // C'est fragile en pur DOM, on va tenter de repérer le bloc adresse.
        
        Elements addressBlocks = doc.select("td:contains(Votre relais Pickup)");
        if (!addressBlocks.isEmpty()) {
            // L'adresse est souvent dans une table suivante ou un td voisin.
            // Simplification: On prend tout le texte et on cherche le pattern d'adresse postale via Regex ?
            // Ou on cherche le nom spécifique extrait par Claude tout à l'heure "EPICERIE..."
        }
        
        // Fallback heuristique simple sur le texte complet pour le nom du relais
        // "disponible à NOM DU RELAIS jusqu'au"
        String text = doc.text();
        Pattern p = Pattern.compile("disponible à\s+(.*?)\s+jusqu'au", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(1).trim();
        }
        
        return "Point Relais (Adresse non détectée)";
    }
}
