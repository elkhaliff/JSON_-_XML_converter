import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        final double m1 = 1.8;
        final double m2 = 32;
        double c = scanner.nextDouble();
        System.out.println(c * m1 + m2);
    }
}
