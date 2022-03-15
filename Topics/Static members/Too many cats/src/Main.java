
class Cat {

    final String name;
    final int age;
    static int counter = 0;
    final int maxCount = 5;

    public Cat(String name, int age) {
        this.name = name;
        this.age = age;
        counter += 1;
        if (counter > maxCount) {
            System.out.println("You have too many cats");
        }
    }

    public static int getNumberOfCats() {
        return counter;
    }
}