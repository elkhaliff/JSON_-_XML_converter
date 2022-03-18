package converter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
//        File file = new File("d:\\test\\test.txt");
        File file = new File("test.txt");
        StringBuilder sb = new StringBuilder();
        try ( Scanner scanner = new Scanner(file) ) {
            while (scanner.hasNext())
                sb.append(scanner.nextLine());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String input = sb.toString().replaceAll("\\s+", " ");

        BrokerXJ broker = new BrokerXJ(input);
        broker.getData();
        broker.print();
    }
}
