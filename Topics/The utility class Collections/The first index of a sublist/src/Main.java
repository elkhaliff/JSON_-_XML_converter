import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.stream.Collectors;

class Main {
    private static ArrayList<Integer> readArrayList(Scanner scanner) {
        return Arrays
                .stream(scanner.nextLine().split("\\s+"))
                .map(Integer::parseInt)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Integer> source = readArrayList(scanner);
        ArrayList<Integer> target = readArrayList(scanner);
        System.out.println(Collections.indexOfSubList(source, target) + " " +
                Collections.lastIndexOfSubList(source, target));
    }
}
