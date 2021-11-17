import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        final String part = "(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])";
        String regex = "(" + part + "\\.){3}" + part;

        String ip = scanner.nextLine();
        System.out.println(ip.matches(regex) ? "YES" : "NO");
    }
}