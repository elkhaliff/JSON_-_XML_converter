import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[] arrInt = new int[scanner.nextInt()];
        int max = 0;
        for (int i = 0; i < arrInt.length; i++) {
            arrInt[i] = scanner.nextInt();
            if (i > 0) {
                max = Math.max(max, arrInt[i] * arrInt[i - 1]);
            }
        }
        System.out.println(max);
    }
}