import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRegex {
    public static void main(String[] args) {
        String text = "est disponible à Panier Sympa L Alambic jusqu'au\n                  <strong>lundi 19 janvier 2026</strong>. Passé cette date, il sera";

        Pattern EXPIRATION_DATE_PATTERN = Pattern.compile("(jusqu'au\\s+(?:<[^>]+>)*)\\s*[a-zA-Z]+\\s+\\d+\\s+[a-zA-Z]+\\s+\\d{4}");

        Matcher matcher = EXPIRATION_DATE_PATTERN.matcher(text);
        if (matcher.find()) {
            System.out.println("Match found!");
            System.out.println(matcher.replaceAll("$1" + " DATE REMPLACEE"));
        } else {
            System.out.println("NO MATCH!");
        }
    }
}
