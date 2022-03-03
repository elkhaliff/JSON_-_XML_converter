import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int[] inp = new int[scan.nextInt()];
        for (int i = 0; i < inp.length; i++) {
            inp[i] = scan.nextInt();
        }
        int test = scan.nextInt();
        for (int elm: inp) {
            if (test == elm) {
                System.out.println(true);
                return;
            }
        }
        System.out.println(false);
    }
}