import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String regNum = scanner.nextLine();

        final String lat = "[ABCEKMHOPTYX]";
        String regNumRegex = lat + "\\d{3}" + lat + "{2}";

        System.out.println(regNum.matches(regNumRegex));
    }
}