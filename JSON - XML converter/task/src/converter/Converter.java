package converter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Converter {

    protected final List<String> pathElement = new ArrayList<>();
    private final List<Element> elements = new ArrayList<>();
    protected String attributesKey;
    protected String parentKey;

    final protected int typeEmpty = -1;
    final protected int typeKey = 15;
    final protected int typeValue = 16;
    final protected int typeAttributeKey = 21;
    final protected int typeAttributeValue = 22;
    final protected int typeParentKey = 33;
    final protected int typeParentValue = 34;
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
        Element element = getLastElement();
        if (element.isHasAttributes()) { // bad for usual value
            element.setBadSequenceStatus();
            if (pathElement.size() > 0) {
                absorbPointElement();
                addBadSequenceElement(element.getName(), element.getValue());
                addBadSequenceElement(element.getName(), value);
            }
        } else if (value.length() > 0 && element.isElemJsonStatusNotBab()) {
            element.setValue(value);
        }
    }

    protected void setParentValue(String parentKey, String value) {
        Element element = getLastElement();
        if (element.isElemJsonStatusNotBab()) {
            if (parentKey.equals(element.getName()) &&
                    !element.isElemJsonStatusBadSequence()) {
                if (value.length() > 0) {
                    element.setValue(value);
                }
            } else {
                element.setBadSequenceStatus();
                if (pathElement.size() > 0) { //parentKey.trim().length() > 0
                    absorbPointElement();
                    addBadSequenceElement(parentKey, value);
                }
            }
        }
    }
    
    private void addBadSequenceElement(String key, String value) {
        newElement(key);
        setValue(value);
        absorbSubElement();
    }

    protected void setAttribute(String value) {
        Element element = getLastElement();
        if (attributesKey.trim().length() > 0 && getLastElement().isElemJsonStatusNotBab()) {
            element.addAttributes(attributesKey, value);
        } else {
            element.setBadSequenceStatus();
        }
        attributesKey = "";
    }

    public void newElement(String name) {
        boolean parentStatus = true;
        if (elements.size() > 0) {
            Element element = getLastElement();
            parentStatus = element.isElemJsonStatusNotBab();
            if (element.isHasAttributes()) {
                element.setBadSequenceStatus();
                absorbPointElement();
                addBadSequenceElement(element.getName(), element.getValue());
                element.setValue("");
            }
        }
        pathElement.add(name);
        boolean statOK = parentStatus && name != null && name.trim().length() > 0;
        elements.add(new Element(new ArrayList(pathElement), statOK));
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
        if (element.isElemJsonStatusBadSequence()) {
            Map<String, String> attrs = new LinkedHashMap<>(element.getAttributes());
            element.clearAttributes();
            element.setBadSequenceStatus();
            for (var attrib : attrs.entrySet()) {
                addBadSequenceElement(attrib.getKey(), attrib.getValue());
            }
        } else {
            absorbSubElement();
        }
    }

    protected abstract void parser(String input);
}