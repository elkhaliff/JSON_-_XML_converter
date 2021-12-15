class Main {
    public static void main(String[] args) {
        char[] chars = new java.util.Scanner(System.in).nextLine().toCharArray();
        boolean res = true;
        for (int i = 1; i < chars.length; i++) {
            if (res) {
                res = chars[i - 1] + 1 == chars[i];
            }
        }
        System.out.println(res);
    }
}