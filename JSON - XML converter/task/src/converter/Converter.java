package converter;

public abstract class Converter {

    public static final int dtXML = 0;
    public static final int dtJSON = 1;

    protected int dataType;

    private Element root;

    public Element getRoot() {
        return root;
    }

    public void parsData(String input) {
        root = parser(input.trim());
    }

    protected abstract Element parser(String input);

    protected abstract boolean check(String src);

    protected abstract String print(Element element);
}