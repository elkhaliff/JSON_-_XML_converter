import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        StringBuilder sb = new StringBuilder(scan.nextLine());
        System.out.println(sb.toString().equals(sb.reverse().toString()) ? "yes" : "no");
    }
}