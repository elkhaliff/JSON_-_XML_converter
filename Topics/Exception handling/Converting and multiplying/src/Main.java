import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        final int ten = 10;
        String str = "";
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                str = scanner.nextLine();
                if ("0".equals(str)) {
                    break;
                }
                System.out.println(Integer.parseInt(str) * ten);
            } catch (Exception e) {
                System.out.println("Invalid user input: " + str);
            }
        }
    }
}