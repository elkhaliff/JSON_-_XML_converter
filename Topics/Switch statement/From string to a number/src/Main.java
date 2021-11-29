import java.util.HashMap;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String str = scanner.nextLine();
        HashMap<String, String> numbers = new HashMap();
        numbers.put("one", "1");
        numbers.put("two", "2");
        numbers.put("three", "3");
        numbers.put("four", "4");
        numbers.put("five", "5");
        numbers.put("six", "6");
        numbers.put("seven", "7");
        numbers.put("eight", "8");
        numbers.put("nine", "9");
        System.out.println(numbers.get(str));
    }
}