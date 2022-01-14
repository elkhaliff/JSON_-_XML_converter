import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        final int three = 3;
        final int one = 1;
        final int two = 2;

        Scanner scanner = new Scanner(System.in);
        int number = scanner.nextInt();
        System.out.print(number + " ");
        while (number != 1) {
            number = number % 2 == 0 ? number / two : number * three + one;
            System.out.print(number + " ");
        }
    }
}