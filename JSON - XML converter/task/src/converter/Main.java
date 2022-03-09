package converter;

import java.io.File;
import java.util.Scanner;

// TODO: законченно 4 задание трека

public class Main {
    public static void main(String[] args) {
//        File file = new File("d:\\test\\test.json");
        File file = new File("test.txt");
        StringBuilder sb = new StringBuilder();
        try ( Scanner scanner = new Scanner(file) ) {
            while (scanner.hasNext())
                sb.append(scanner.nextLine());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String input = sb.toString().replaceAll("\\s+", " ");
//        System.out.println(input);

        BrokerXJ broker = new BrokerXJ(input);
        broker.getData();
        broker.print();
    }
}