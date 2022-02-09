import java.util.*;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int row = scanner.nextInt();
        scanner.nextLine();
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < row; i++) {
            strings.add(scanner.nextLine());
        }
        int cnt = scanner.nextInt();
        Collections.rotate(strings, cnt);
        strings.forEach(System.out::println);
    }
}