public class Main {

    public static void main(String[] args) {
        int cnt = 0;
        for (Secret word : Secret.values()) {
            if (word.name().startsWith("STAR")) {
                cnt++;
            }
        }
        System.out.print(cnt);
    }
}