import java.util.*;
import java.util.stream.Collectors;
import java.util.Collections;

public class Main {

    public static void sortInDescendingOrder(List<Integer> sequence) {
        Collections.sort(sequence);
        Collections.reverse(sequence);
    }

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final List<Integer> seq = Arrays.stream(scanner.nextLine().split("\\s+"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        sortInDescendingOrder(seq);
        seq.forEach(e -> System.out.print(e + " "));
    }
}