import java.util.Scanner;

class ManufacturingController {
    private static int numberOfProducts;

    public static String requestProduct(String product) {
        numberOfProducts++;
        return numberOfProducts + ". Requested " + product;
    }

    public static int getNumberOfProducts() {
        return numberOfProducts;
    }
}

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String product = scanner.nextLine();
            System.out.println(ManufacturingController.requestProduct(product));
            System.out.println(ManufacturingController.getNumberOfProducts());
        }
    }
}