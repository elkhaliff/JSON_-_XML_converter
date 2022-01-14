import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        final int percent = 100;
        float m = scanner.nextInt();
        float p = scanner.nextInt();
        float k = scanner.nextInt();
        int count = 0;
        while (m < k) {
            m += m * p / percent;
            count++;
        }
        System.out.println(count);
    }
}