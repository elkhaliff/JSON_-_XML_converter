package converter;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        File file = new File("d:\\test\\test1.txt");
        StringBuilder sb = new StringBuilder();
        try (
            Scanner scanner = new Scanner(file);
        ) {
            while (scanner.hasNext()) {
                sb.append(scanner.nextLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String output;
        String input = sb.toString(); //scanner.nextLine().trim(); //.replace(" ", "")
        if (input.toCharArray()[0] == '<')
            output = convertToJSON(input);
        else
            output = convertToXML(input);
        System.out.println(output);
    }

    private static String convertToXML(String input) {
        JSONObject json = new JSONObject(input.replace("null", "\"\""));
        return XML.toString(json);
    }

    private static String convertToJSON(String input) {
        return XML.toJSONObject(input).toString().replace("\"\"", "null");
    }
}
