import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.next();
        int n = scanner.nextInt();
        System.out.println(n < s.length() ? s.substring(n) + s.substring(0, n) : s);
    }
}
