import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int army = scanner.nextInt();
        final int pack = 19;
        final int throng = 249;
        final int zounds = 999;
        String category;
        if (army < 1) {
            category = "no army";
        } else if (army <= pack) {
            category = "pack";
        } else if (army <= throng) {
            category = "throng";
        } else if (army <= zounds) {
            category = "zounds";
        } else {
            category = "legion";
        }
        System.out.println(category);
    }
}