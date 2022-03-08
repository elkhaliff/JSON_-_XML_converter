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


    final private int statOK = 0;
    final private int statBadElement = 1;
    final private int statBadSequence = 2;

    protected int jsonStatus;

    public Element(List<String> path, boolean isElementOK) {
        value = "";
        this.path = path;
        jsonStatus = isElementOK ? statOK : statBadElement;
    }

    public boolean isElemJsonStatusOK() { return jsonStatus == statOK; }

    public boolean isElemJsonStatusNotBab() { return jsonStatus != statBadElement; }

    public void setBadSequenceStatus() {
        jsonStatus = statBadSequence;
    }

    public boolean isHasAttributes() {
        return attributes.size() > 0;
    }

    public void addAttributes(String key, String value) {
        attributes.put(key, value);
    }

    public Map getAttributes() { return attributes; }

    public void clearAttributes() { attributes.clear(); }

    public void setValue(String value) { this.value = value; }

    public void addSub(Element element) {
        subElements.add(element);
    }

    public String getName() { return path.get(path.size() - 1); }

    @Override
    public String toString() {
        if (isElemJsonStatusNotBab()) {
            StringBuilder sb = new StringBuilder("Element:\n");
            sb.append("path = ");
            for (String val : path) {
                sb.append(String.format("%s, ", val));
            }
            sb.delete(sb.length() - 2, sb.length());
            sb.append("\n");
            if (value.equals("null")) {
                sb.append("value = null\n");
            //} else if (value != null ) {
            } else if (value.length() > 0 ) {
                sb.append(String.format("value = \"%s\"\n", value));
            }
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
        } else
            return "";
    }
}
