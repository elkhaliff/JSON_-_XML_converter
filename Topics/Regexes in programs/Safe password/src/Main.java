import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String regex = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{12,}";

        String passwd = scanner.nextLine();
        System.out.println(passwd.matches(regex) ? "YES" : "NO");
    }
}