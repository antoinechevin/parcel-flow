import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TestRegexVinted {
    public static void main(String[] args) {
        String text = "retirer avant le <b>05/03/2026</b>.";

        Pattern VINTED_EXPIRATION = Pattern.compile("(retirer avant le\\s*(?:<[^>]+>)*)\\s*\\d{2}/\\d{2}/\\d{4}");

        Matcher matcher = VINTED_EXPIRATION.matcher(text);
        if (matcher.find()) {
            System.out.println("Match found!");
            String future = ZonedDateTime.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            System.out.println(matcher.replaceAll("$1" + future));
        } else {
            System.out.println("NO MATCH!");
        }
    }
}
