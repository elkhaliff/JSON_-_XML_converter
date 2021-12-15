package converter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Converter {

    private final Map<String, Object> tree = new LinkedHashMap<>();
    protected final Map<String, Object> branch = new LinkedHashMap<>();
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

    public void clear() {
        parsType = typeEmpty;
    }

    protected void setData4LastKey(Object obj, boolean deleteKey) {
        setData4LastKey(obj, null, deleteKey);
    }

    protected void setData4LastKey(Object obj, String prefix, boolean deleteKey) {
        String pref = prefix == null ? "" : prefix;
        if (path.size() > 0) {
            int lastInd = path.size() - 1;
            if (lastInd > 0) {
                branch.put(pref + path.get(lastInd), obj);
            } else
                tree.put(path.get(lastInd), obj);
            if (deleteKey)
                path.remove(lastInd);
        }
    }

    public Map<String, Object> getData(String input) {
        parser(input.trim());
        return tree;
    }

    protected StringBuilder saveValueAndClearSB(StringBuilder sb) {
        String tmp = sb.toString().trim();
        if (tmp.length() > 0) setData4LastKey(tmp, false);
        return new StringBuilder();
    }

    public List getElements() {
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