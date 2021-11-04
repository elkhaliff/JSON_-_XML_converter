package converter;

import org.json.JSONObject;
import org.json.XML;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String output = "";
        String input = scanner.nextLine().trim();
        if (input.toCharArray()[0] == '<')
            output = converToJSON(input);
        else
            output = convertToXML(input);
        System.out.println(output);
    }

    private static String convertToXML(String input) {
        JSONObject json = new JSONObject(input.replace("null", "\"\""));
        return XML.toString(json);
    }

    private static String converToJSON(String input) {
        return XML.toJSONObject(input).toString().replace("\"\"", "null");
    }
}
