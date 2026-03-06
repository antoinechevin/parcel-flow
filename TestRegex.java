import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRegex {
    public static void main(String[] args) {
        String chronoMail = "  N° de colis <strong>XW12345678TS</strong><br />\n";
        String chronoMail2 = "  n° <a href=\"#\">XW12345678TS</a>";
        Pattern CHRONOPOST_TRACKING = Pattern.compile("(n°(?:\\s*(?:de colis)?\\s*<[^>]+>\\s*|\\s+))([A-Z0-9]{10,20})", Pattern.CASE_INSENSITIVE);

        System.out.println("1: " + CHRONOPOST_TRACKING.matcher(chronoMail).replaceAll("$1MOCK-$2"));
        System.out.println("2: " + CHRONOPOST_TRACKING.matcher(chronoMail2).replaceAll("$1MOCK-$2"));
    }
}
