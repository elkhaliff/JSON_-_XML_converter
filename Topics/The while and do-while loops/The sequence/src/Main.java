import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int cnt = scanner.nextInt();
        int max = Integer.MIN_VALUE;
	for (int i = 0; i < cnt; i++) {
            int r = scanner.nextInt();
	    if (r % 4 == 0 && r > max) {
                max = r;
	    }
	}
	System.out.println(max);
    }
}
