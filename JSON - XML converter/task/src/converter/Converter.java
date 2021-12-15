package converter;

import java.util.ArrayList;
import java.util.List;

public abstract class Converter {

    protected final List<String> path = new ArrayList<>();
    private final List<Element> elements = new ArrayList<>();
    protected String keyAttributes;

    final protected int typeEmpty = -1;
    final protected int typeKey = 0;
    final protected int typeAttributeKey = 11;
    final protected int typeAttributeValue = 12;
    final protected int typeEnd = 2;
    final protected int typeValue = 3;
    protected int parsType;

    public void getData(String input) {
        parser(input.trim());
    }

    public List<Element> getElements() {
        return elements;
    }

    private Element getLastElement() {
        return elements.get(elements.size() - 1);
    }

    protected void setValue(String value) {
        if (value.length() > 0) {
            Element element = getLastElement();
            element.setValue(value);
        }
    }

    protected void setAttribute(String value) {
        Element element = getLastElement();
        element.addAttributes(keyAttributes, value);
        keyAttributes = "";
    }

    public StringBuilder newElement(StringBuilder elementName) {
        String name = elementName.toString().trim();
        if (name.length() > 0) {
            path.add(name);
            elements.add(new Element(new ArrayList(path)));
        }
        return new StringBuilder();
    }

    protected void addSub2Parent() {
        if (elements.size() > 1) {
            Element subElm = getLastElement();
            elements.remove(elements.size() - 1);
            path.remove(path.size() - 1);
            Element parentElm = getLastElement();
            parentElm.addSub(subElm);
        }
    }

    protected abstract void parser(String input);
}