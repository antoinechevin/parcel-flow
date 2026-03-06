import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.Locale;

public class TestEmlRegex {
    public static void main(String[] args) throws Exception {
        String mockedHtml = "Panier Sympa L Alambic jusqu'au\n                    <strong> mercredi 11 mars 2026</strong>. Passé cette date";

        Document doc = Jsoup.parse(mockedHtml);
        String text = doc.text();
        System.out.println("Parsed text: '" + text + "'");

        Pattern DATE_PATTERN = Pattern.compile("jusqu'au\\s+(?:<strong>)?(.*?)(?:</strong>)?\\.", Pattern.CASE_INSENSITIVE);
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);

        Matcher matcher = DATE_PATTERN.matcher(text);
        if (matcher.find()) {
            System.out.println("Adapter match found: '" + matcher.group(1) + "'");
            String dateStr = matcher.group(1).trim().toLowerCase();
            dateStr = Jsoup.parse(dateStr).text();
            System.out.println("Adapter date string: '" + dateStr + "'");
            try {
                LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
                System.out.println("SUCCESS: " + date);
            } catch (Exception e) {
                System.out.println("FAIL: " + e.getMessage());
            }
        } else {
            System.out.println("NO ADAPTER MATCH");
        }
    }
}
