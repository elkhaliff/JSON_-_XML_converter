package converter;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertJSON extends Converter {
    private static final Pattern JSON_BEGIN = Pattern.compile("(?s)^\\s*\\{\\s*[\"}]");

    private static final Pattern OBJECT_BEGIN = Pattern.compile("(?s)^\\s*\\{\\s*");
    private static final Pattern OBJECT_END = Pattern.compile("(?s)^\\s*}\\s*,?");
    private static final Pattern OBJECT_ATTRIB_NAME = Pattern.compile("(?s)^\\s*\"(.*?)\"\\s*:\\s*");
    private static final Pattern OBJECT_ATTRIB_VALUE = Pattern.compile("(?s)^\\s*(\"(.*?)\"|(\\d+\\.?\\d*)|(null)),?");

    private static final Pattern PATTERN_EXT_ATTR = Pattern.compile("(?i)^[#@][a-z_][.\\w]*");
    private static final Pattern PATTERN_EXT_IDENT = Pattern.compile("(?i)^[a-z_][.\\w]*");

    @Override
    protected Element parser(String input) {
        return parsElements(new Finder(input), new Element());
    }

    private Element parsElements(Finder finder, Element parent) {
        if (!finder.next(OBJECT_BEGIN)) {
            return null;
        }

        Element element;
        Matcher matcher;
        while (finder.next(OBJECT_ATTRIB_NAME)) {
            element = new Element(finder.getMatcher().group(1));
            if (finder.check(JSON_BEGIN)) {
                parsElements(finder, element);
                parsObject(element);

            } else if (finder.next(OBJECT_ATTRIB_VALUE)) {
                matcher = finder.getMatcher();
                if (matcher.group(2) != null) { // string
                    element.setValue(matcher.group(2));
                } else if (matcher.group(3) != null) { // number
                    element.setValue(matcher.group(3));
                } else if (matcher.group(4) != null) { // null
                    element.setValue(null);
                } else {
                    throw new RuntimeException("Unknown attribute value.");
                }
            } else {
                throw new RuntimeException("Attribute value expected.");
            }
            parent.addSub(element);
        }

        if (!finder.next(OBJECT_END)) {
            throw new RuntimeException("Object end expected.");
        }

        return parent;
    }

    @Override
    public boolean check(String input) {
        return JSON_BEGIN.matcher(input).find();
    }


    private static boolean isValidExtAttributes(String name) {
        return name != null && PATTERN_EXT_ATTR.matcher(name).matches();
    }

    private static boolean isValidExtIdentifier(String name) {
        return name != null && PATTERN_EXT_IDENT.matcher(name).matches();
    }

    private static boolean isExtAttributes(Element element) {
        Map<String, Element> subMap = element.getSubMap();
        if (!subMap.containsKey("#" + element.getName())) {
            return false;
        }
        for (Map.Entry<String, Element> subElem : subMap.entrySet()) {
            if (!isValidExtAttributes(subElem.getKey())) {
                return false;
            }
            if (subElem.getKey().charAt(1) == '@' && subElem.getValue().hasSub()) {
                return false;
            }
        }
        return true;
    }

    private static void parsObject(Element parent) {
        Element element;
        if (isExtAttributes(parent)) {
            for (Map.Entry<String, Element> elem : parent.getSubMap().entrySet()) {
                element = elem.getValue();
                if (elem.getKey().charAt(0) == '#') {
                    if (element.hasSub()) {
                        parent.removeSub(element);
                        for (Element subElm: element.getSubMap().values()) {
                            parent.addSub(subElm);
                        }
                    } else {
                        element = parent.removeSub(elem.getValue());
                        parent.setValue(element.getValue());
                    }
                } else {
                    element = parent.removeSub(elem.getValue());
                    parent.setAttribute(element.getName().substring(1), element.getValue());
                }
            }

        } else {
            Map<String, Element> childrenMap = parent.getSubMap();
            for (Map.Entry<String, Element> elem : childrenMap.entrySet()) {
                if (isValidExtAttributes(elem.getKey())) {
                    if (childrenMap.containsKey(elem.getKey().substring(1))) {
                        parent.removeSub(elem.getValue());
                    } else {
                        elem.getValue().setName(elem.getValue().getName().substring(1));
                    }
                } else if (!isValidExtIdentifier(elem.getKey())) {
                    parent.removeSub(elem.getValue());
                }
            }
            if (!parent.hasSub()) {
                parent.setValue("");
            }
        }

    }

    @Override
    public String print(Element element) {
        return printElement(new StringBuilder(), element).toString();
    }

    private static StringBuilder printElement(StringBuilder out, Element element) {
        if (element.hasAttributes()) {
            out.append("{\n");

            for (Map.Entry<String, String> elem: element.getAttributes().entrySet()) {
                out.append(String.format("\"@%s\" : \"%s\",\n", elem.getKey(), elem.getValue()));
            }

            if (element.hasSub()) {
                out.append(String.format("\"#%s\": ", element.getName()));
                printSub(out, element);
            } else if (element.getValue() == null) {
                out.append(String.format("\"#%s\" : null\n", element.getName()));
            } else {
                out.append(String.format("\"#%s\" : \"%s\"\n", element.getName(), element.getValue()));
            }

            out.append(" }");

        } else if (element.hasSub()) {
            printSub(out, element);

        } else if (element.getValue() == null) {
            out.append("null");

        } else {
            out.append(String.format("\"%s\"", element.getValue()));

        }

        return out;
    }

    private static void printSub(StringBuilder out, Element parent) {
        if (parent.hasSub()) {
            out.append("{\n");
        }

        Element element;
        for (int i = 0; i < parent.getSubElements().size(); i++) {
            element = parent.getSubElements().get(i);
            out.append(String.format("\"%s\" : ", element.getName()));
            printElement(out, element);
            if (i != parent.getSubElements().size() - 1) {
                out.append(",\n");
            }
        }

        if (parent.hasSub()) {
            out.append(" }");
        }
    }
}