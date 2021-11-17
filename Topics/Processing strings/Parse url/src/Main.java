import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] parts = scanner.nextLine().split("\\?")[1].split("&");
        String passwd = "";
        for (String part: parts) {
            String[] out = part.split("=");
            passwd = "pass".equals(out[0]) ? "password : " + out[1] : passwd;
            System.out.println(out[0] + " : " + (out.length < 2 ? "not found" : out[1]));
        }
        System.out.println(passwd);
    }
}