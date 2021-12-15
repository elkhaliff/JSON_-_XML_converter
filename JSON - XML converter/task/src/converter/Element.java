package converter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Element {
    private final List<String> path;
    private final Map<String, String> attributes = new LinkedHashMap<>();
    private String value;
    private List<Element> subElements = new ArrayList<>();

    public Element(List<String> path) {
        this.path = path;
    }

    public void addAttributes(String key, String value) {
        attributes.put(key, value);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void addSub(Element element) {
        subElements.add(element);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Element:\n");
        sb.append("path = ");
        for (String val : path) {
            sb.append(String.format("%s, ", val));
        }
        sb.delete(sb.length()-2, sb.length());
        sb.append("\n");
        if (value != null) {
            sb.append(String.format("value = \"%s\"\n", value));
        } else
            sb.append("value = null\n");
        if (attributes.size() > 0) {
            sb.append("attributes:\n");
            for (var entry : attributes.entrySet()) {
                sb.append(String.format("%s = \"%s\"\n", entry.getKey(), entry.getValue()));
            }
        }
        sb.append("\n");
        if (subElements.size() > 0) {
            for (Element element : subElements) {
                sb.append(element);
            }
        }
        return sb.toString();
    }
}
