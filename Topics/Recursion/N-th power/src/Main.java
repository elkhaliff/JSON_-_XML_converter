import java.util.Scanner;

public class Main {

    public static double pow(double a, long n) {
        return n == 0 ? 1d : n % 2 == 0 ? pow(a * a, n / 2) : a * pow(a, n - 1);
    }

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final double a = Double.parseDouble(scanner.nextLine());
        final int n = Integer.parseInt(scanner.nextLine());
        System.out.println(pow(a, n));
    }
}