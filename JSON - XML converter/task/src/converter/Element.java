package converter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Element {
    private String name;
    private String value;
    private final int dataType;


    private final Map<String, String> attributes = new LinkedHashMap<>();
    private final List<Element> subElements = new ArrayList<>();

    public Element(int dataType) {
        this(dataType, null);
    }

    public Element(int dataType, String name) {
        this.dataType = dataType;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Element addSub(String name) {
        return addSub(new Element(dataType, name));
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (getName() != null) {
            switch (dataType) {
                case Converter.dtXML: {
                    sb.append(String.format("\"%s\": ", getName()));

                    if (!attributes.isEmpty()) {
                        sb.append("{\n");
                        for (var attr : attributes.entrySet()) {
                            sb.append(String.format("\"@%s\": ", attr.getKey()))
                                    .append(String.format("%s,\n", attr.getValue() == null ? "null" : String.format("\"%s\"", attr.getValue())));
                        }
                        sb.append(String.format("\"#%s\": ", getName()));
                        if (!subElements.isEmpty()) {
                            sb.append("{\n");
                            for (int e = 0; e < subElements.size(); e++) {
                                sb.append(String.format("%s", subElements.get(e).toString()));
                                if (e < subElements.size() - 1)
                                    sb.append(",");
                                sb.append("\n");
                            }
                            sb.append("}\n");
                        } else {
                            sb.append(String.format("%s\n", value == null ? "null" : String.format("\"%s\"", value)));
                        }
                        sb.append("}");
                    } else {
                        if (!subElements.isEmpty()) {
                            sb.append("{\n");
                            for (int e = 0; e < subElements.size(); e++) {
                                sb.append(String.format("%s", subElements.get(e).toString()));
                                if (e < subElements.size() - 1)
                                    sb.append(",");
                                sb.append("\n");
                            }
                            sb.append("}");
                        } else {
                            sb.append(String.format("%s", value == null ? "null" : String.format("\"%s\"", value)));
                        }
                    }
                    break;
                }
                case Converter.dtJSON: {
                    sb.append(String.format("\n<%s", getName()));

                    if (!attributes.isEmpty()) {
                        for (var attr : attributes.entrySet()) {
                            sb.append(String.format(" %s=\"%s\"", attr.getKey(), attr.getValue() == null ? "" : attr.getValue()));
                        }
                    }

                    if (!subElements.isEmpty()) {
                        sb.append(">");
                        for (Element element : subElements) {
                            sb.append(element.toString());
                        }
                        sb.append(String.format("\n</%s>", getName()));
                    } else {
                        if (value == null)
                            sb.append(" />");
                        else {
                            sb.append(">");
                            sb.append(String.format("%s", value))
                              .append(String.format("</%s>", getName()));
                        }
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        } else {
            for (Element element : subElements) {
                sb.append(element.toString());
            }
        }

        return sb.toString();
    }
}
