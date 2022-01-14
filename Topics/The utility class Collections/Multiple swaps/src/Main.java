import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.stream.Collectors;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> strings = Arrays
                .stream(scanner.nextLine().split("\\s+"))
                .collect(Collectors.toCollection(ArrayList::new));
        int cnt = scanner.nextInt();
        for (int i = 0; i < cnt; i++) {
            Collections.swap(strings, scanner.nextInt(), scanner.nextInt());
        }
        strings.forEach(e -> System.out.print(e + " "));
    }
}