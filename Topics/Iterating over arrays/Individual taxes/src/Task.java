// write your answer here 
import java.util.Scanner;

public class Task {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int countComp = scanner.nextInt();
        double maxTaxes = Double.MIN_VALUE;
        double[] incomes = new double[countComp];
        int maxIndex = 0;
        for (int i = 0; i < countComp; i++) {
            incomes[i] = scanner.nextDouble();
        }
        for (int i = 0; i < countComp; i++) {
            double tax = incomes[i] * scanner.nextDouble();
            if (maxTaxes < tax) {
                maxTaxes = tax;
                maxIndex = i;
            }
        }
        System.out.println(maxIndex + 1);
    }
}
