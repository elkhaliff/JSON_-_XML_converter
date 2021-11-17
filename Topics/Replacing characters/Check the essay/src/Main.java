import java.util.Scanner;


class CheckTheEssay {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String text = scanner.nextLine();

        text = text.replaceAll("Franse", "France");
        text = text.replaceAll("Eifel tower", "Eiffel Tower");
        text = text.replaceAll("19th", "XIXth");
        text = text.replaceAll("20th", "XXth");
        text = text.replaceAll("21st", "XXIst");
        System.out.println(text);
    }
}