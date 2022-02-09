package converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Converter {

    protected final List<String> pathElement = new ArrayList<>();
    private final List<Element> elements = new ArrayList<>();
    protected String keyAttributes;

    final protected int typeEmpty = -1;
    final protected int typeKey = 1;
    final protected int typeValue = 2;
    final protected int typeAttributeKey = 11;
    final protected int typeAttributeValue = 12;
    final protected int typeParentKey = 21;
    final protected int typeParentValue = 22;
    final protected int typeEnd = 3;
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

    protected void setParentValue(String value) {
        Element element = getLastElement();
        if (element.isHasAttributes()) {
            if (value.length() > 0) {
                element.setValue(value);
            }
        } else {
            element.setBadSequenceStatus();
            if (pathElement.size() > 0) {
                addBadSequenceElement(pathElement.get(pathElement.size() - 1), value);
            }
        }
    }
    
    private void addBadSequenceElement(String key, String value) {
        newElement(key);
        setValue(value);
        absorbSubElement();
    }

    protected void setAttribute(String value) {
        if (keyAttributes.trim().length() > 0) {
            Element element = getLastElement();
            element.addAttributes(keyAttributes, value);
        }
        keyAttributes = "";
    }

    public void newElement(String name) {
        pathElement.add(name);
        elements.add(new Element(new ArrayList(pathElement), name.trim().length() > 0));
    }

    protected void absorbSubElement() {
        if (pathElement.size() > 1) {
            Element subElm = getLastElement();
            elements.remove(elements.size() - 1);
            Element parentElm = getLastElement();
            parentElm.addSub(subElm);
        }
        if (pathElement.size() > 0)
            pathElement.remove(pathElement.size() - 1);

    }

    protected void absorbPointElement() {
        Element element = getLastElement();
        if (element.isHasAttributes()) {
            absorbSubElement();
        } else {
            Map<String, String> attrs = element.getAttributes();
            element.setBadSequenceStatus();
            for (var attrib : attrs.entrySet()) {
                addBadSequenceElement(attrib.getKey(), attrib.getValue());
            }
            element.clearAttributes();
        }
    }

    protected abstract void parser(String input);
}