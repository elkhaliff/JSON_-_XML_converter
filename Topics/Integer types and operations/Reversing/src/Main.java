import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder sb = new StringBuilder(scanner.nextLine()).reverse();
        System.out.println(Integer.parseInt(sb.toString()));
    }
}