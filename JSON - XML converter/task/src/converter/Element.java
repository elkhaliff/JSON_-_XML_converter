package converter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Element {
    private String name;
    private String value;

    private final Map<String, String> attributes = new LinkedHashMap<>();
    private final List<Element> subElements = new ArrayList<>();

    public Element() {
        this(null);
    }

    public Element(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Element addSub(String name) {
        return addSub(new Element(name));
    }

    public Element addSub(Element subElement) {
        subElements.add(subElement);
        return subElement;
    }

    public Element removeSub(Element element) {
        int i = subElements.indexOf(element);
        if (i < 0) {
            return null;
        }
        return subElements.remove(i);
    }

    public boolean hasSub() {
        return !subElements.isEmpty();
    }

    public Map<String, Element> getSubMap() {
        Map<String, Element> map = new LinkedHashMap<>();
        for (Element element : subElements) {
            map.put(element.getName(), element);
        }
        return map;
    }

    public List<Element> getSubElements() {
        return subElements;
    }
}
