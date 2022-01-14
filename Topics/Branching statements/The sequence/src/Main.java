import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int cnt = scanner.nextInt();
        int step = cnt;
        for (int i = 1; i <= cnt; i++) {
            for (int k = 0; k < i; k++) {
                if (step == 0) {
                    break;
                } else {
                    System.out.print(i + " ");
                    step--;
                }
            }
        }
    }
}