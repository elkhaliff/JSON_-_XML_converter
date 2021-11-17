import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String stringWithNumbers = scanner.nextLine();

        Pattern pattern = Pattern.compile("\\b\\d{10,}\\b");
        Matcher matcher = pattern.matcher(stringWithNumbers);

        while (matcher.find()) {
            String big = matcher.group();
            System.out.println(big + ":" + big.length());
        }
    }
}