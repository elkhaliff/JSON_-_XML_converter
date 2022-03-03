import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] str = scanner.nextLine().split(" ");
        for (int i = 1; i < str.length; i++) {
            if (str[i].compareTo(str[i - 1]) < 0) {
                System.out.println("false");
                return;
            }
        }
        System.out.println("true");
    }
}