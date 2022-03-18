package converter;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertJSON extends Converter {
    private static final Pattern PATTERN_JSON_BEGIN = Pattern.compile("(?s)^\\s*\\{\\s*[\"}]");
    private static final Pattern PATTERN_JSON_OPEN = Pattern.compile("(?s)^\\s*\\{\\s*");
    private static final Pattern PATTERN_JSON_CLOSE = Pattern.compile("(?s)^\\s*}\\s*,?");
    private static final Pattern PATTERN_JSON_ATTR_NAME = Pattern.compile("(?s)^\\s*\"(.*?)\"\\s*:\\s*");
    private static final Pattern PATTERN_JSON_ATTR_VALUE = Pattern.compile("(?s)^\\s*(\"(.*?)\"|(\\d+\\.?\\d*)|(null)),?");

    private static final Pattern PATTERN_EXT_ATTR = Pattern.compile("(?i)^[#@][a-z_][.\\w]*");
    private static final Pattern PATTERN_EXT_IDENT = Pattern.compile("(?i)^[a-z_][.\\w]*");

    @Override
    protected Element parser(String input) {
        dataType = dtJSON;
        Element root = new Element(dataType);
        parsElements(input, root, 0);
        return root;
    }

    private int parsElements(String input, Element parent, int position) {
        Matcher openMatcher = PATTERN_JSON_OPEN
                .matcher(input)
                .region(position, input.length())
                .useAnchoringBounds(true);

        if (!openMatcher.find()) {
            return position;
        }

        int index = openMatcher.end();

        Matcher attrMatcher = PATTERN_JSON_ATTR_NAME
                .matcher(input)
                .useAnchoringBounds(true)
                .region(index, input.length());

        Matcher valueMatcher = PATTERN_JSON_ATTR_VALUE
                .matcher(input)
                .useAnchoringBounds(true);

        Matcher closeMatcher = PATTERN_JSON_CLOSE
                .matcher(input)
                .useAnchoringBounds(true);

        Element element;
        while (attrMatcher.find()) {
            index = attrMatcher.end();
            element = new Element(dataType, attrMatcher.group(1));
            if (isJson(input, index)) {
                index = parsElements(input, element, index);

                if (isExtAttributes(element)) {
                    Element subElement;
                    for (Map.Entry<String, Element> elem : element.getSubMap().entrySet()) {
                        subElement = elem.getValue();
                        if (elem.getKey().charAt(0) == '#') {
                            if (subElement.hasSub()) {
                                element.removeSub(subElement);
                                for (Element subElm: subElement.getSubMap().values()) {
                                    element.addSub(subElm);
                                }
                            } else {
                                subElement = element.removeSub(elem.getValue());
                                element.setValue(subElement.getValue());
                            }
                        } else {
                            subElement = element.removeSub(elem.getValue());
                            element.setAttribute(subElement.getName().substring(1), subElement.getValue());
                        }
                    }

                } else {
                    Map<String, Element> subMap = element.getSubMap();
                    for (Map.Entry<String, Element> elem : subMap.entrySet()) {
                        if (isValidExtAttributes(elem.getKey())) {
                            if (subMap.containsKey(elem.getKey().substring(1))) {
                                element.removeSub(elem.getValue());
                            } else {
                                elem.getValue().setName(elem.getValue().getName().substring(1));
                            }
                        } else if (!isValidExtIdentifier(elem.getKey())) {
                            element.removeSub(elem.getValue());
                        }
                    }
                    if (!element.hasSub()) {
                        element.setValue("");
                    }
                }

            } else {
                valueMatcher.region(index, input.length());
                if (!valueMatcher.find()) {
                    throw new RuntimeException("Attribute value expected.");
                }

                if (valueMatcher.group(2) != null) { // string
                    element.setValue(valueMatcher.group(2));

                } else if (valueMatcher.group(3) != null) { // number
                    element.setValue(valueMatcher.group(3));

                } else if (valueMatcher.group(4) != null) { // null
                    element.setValue(null);

                } else {
                    throw new RuntimeException("Unknown attribute value.");

                }
                index = valueMatcher.end();
            }
            attrMatcher.region(index, input.length());

            parent.addSub(element);
        }

        closeMatcher.region(index, input.length());
        if (!closeMatcher.find()) {
            throw new RuntimeException("Object end expected.");
        }

        return closeMatcher.end();
    }

    public static boolean isJson(String src) {
        return isJson(src, 0);
    }

    private static boolean isJson(String src, int start) {
        return PATTERN_JSON_BEGIN
                .matcher(src)
                .region(start, src.length())
                .useAnchoringBounds(true)
                .find();
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
}