package converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertXML extends Converter {
    private static final Pattern PATTERN_XML_BEGIN = Pattern.compile("(?s)\\A\\s*<\\s*[a-z_]\\w+");
    private static final Pattern PATTERN_OPEN_TAG = Pattern.compile("(?is)\\s*<\\s*([a-z_]\\w+)\\s*([a-z_]\\w+\\s*=\\s*\".*?\")*\\s*(>|/>)");
    private static final Pattern PATTERN_ATTRIBUTE = Pattern.compile("(?is)([a-z_]\\w+)\\s*=\\s*\"(.*?)\"");

    @Override
    protected Element parser(String input) {
        dataType = dtXML;
        Element root = new Element(dataType);
        parsElements(input, root, 0);
        return root;
    }

    private int parsElements(String input, Element parent, int position) {
        Matcher tagMatcher = PATTERN_OPEN_TAG.matcher(input);
        Matcher attrMatcher;
        Matcher closeTagMatcher;
        Element element;

        int i = position;
        while (tagMatcher.find(i)) {
            element = parent.addSub(tagMatcher.group(1));

            if (tagMatcher.group(2) != null) {
                attrMatcher = PATTERN_ATTRIBUTE.matcher(tagMatcher.group(2));
                while (attrMatcher.find()) {
                    element.setAttribute(attrMatcher.group(1), attrMatcher.group(2));
                }
            }

            i = tagMatcher.end();
            if (">".equals(tagMatcher.group(3))) {
                closeTagMatcher = Pattern
                        .compile(String.format("(?s)(.*?)<\\s*\\/%s\\s*>", element.getName()))
                        .matcher(input);

                if (!closeTagMatcher.find(i)) {
                    throw new RuntimeException("Enclosing tag expected.");
                }

                if (isXml(input, i)) {
                    i = parsElements(input, element, i);
                }

                if (!element.hasSub()) {
                    element.setValue(closeTagMatcher.group(1));
                }

                i = closeTagMatcher.end();
            }
        }

        return i;
    }

    public static boolean isXml(String input) {
        return isXml(input, 0);
    }

    private static boolean isXml(String input, int position) {
        return PATTERN_XML_BEGIN.matcher(input.substring(position)).find();
    }
}