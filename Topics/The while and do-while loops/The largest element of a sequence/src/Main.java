import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int in;
        int max = Integer.MIN_VALUE;
        do {
            in = scanner.nextInt();
            max = Math.max(in, max);
        } while (in != 0);
        System.out.println(max);
    }
}