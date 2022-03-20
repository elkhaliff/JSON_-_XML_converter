package converter;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertXML extends Converter {
    private static final Pattern XML_BEGIN = Pattern.compile("(?s)\\A\\s*<\\s*[a-z_]\\w+");
    private static final Pattern TAG_OPEN = Pattern.compile("(?is)^\\s*<\\s*([a-z_]\\w+)\\s*([a-z_]\\w+\\s*=\\s*\".*?\")*\\s*(>|/>)");
    private static final Pattern ATTRIBUTES = Pattern.compile("(?is)([a-z_]\\w+)\\s*=\\s*\"(.*?)\"");

    @Override
    public boolean check(String input) {
        return XML_BEGIN.matcher(input).find();
    }

    @Override
    protected Element parser(String input) {
        return parsElements(new Finder(input), new Element());
    }

    private Element parsElements(Finder finder, Element parent) {
        Element element;
        Matcher matcher;
        while (finder.next(TAG_OPEN)) {
            matcher = finder.getMatcher();
            element = parent.addSub(matcher.group(1));
            parsAttributes(matcher.group(2), element);

            if (">".equals(matcher.group(3))) {
                if (finder.check(XML_BEGIN)) {
                    parsElements(finder, element);
                }
                if (!finder.next(String.format("(?s)^(.*?)<\\s*\\/%s\\s*>", element.getName()))) {
                    throw new RuntimeException("Enclosing tag expected.");
                }
                if (!element.hasSub()) {
                    element.setValue(finder.getMatcher().group(1));
                }
            }
        }
        return parent;
    }

    private static void parsAttributes(String src, Element element) {
        if (src == null) {
            return;
        }
        Matcher matcher;
        Finder finder = new Finder(src);
        while (finder.next(ATTRIBUTES)) {
            matcher = finder.getMatcher();
            element.setAttribute(matcher.group(1), matcher.group(2));
        }
    }

    @Override
    public String print(Element element) {
        return printElement(new StringBuilder(), element).toString();
    }

    private static StringBuilder printElement(StringBuilder out, Element element) {
        String nodeName = element.getName();
        if (nodeName == null) {
            if (element.getSubElements().size() > 1) {
                out.append("<root>\n");
            }
            for (Element sub : element.getSubElements()) {
                printElement(out, sub);
            }
            if (element.getSubElements().size() > 1) {
                out.append("</root>\n");
            }
            return out;
        }
        out.append(String.format("<%s", nodeName));
        for (Map.Entry<String, String> elem: element.getAttributes().entrySet()) {
            out.append(String.format(" %s = \"%s\"", elem.getKey(), elem.getValue() == null ? "" : elem.getValue()));
        }
        if (element.hasSub()) {
            out.append(">\n");
            for (Element sub : element.getSubElements()) {
                printElement(out, sub);
            }
            out.append(String.format("</%s>\n", nodeName));
        } else if (element.getValue() == null) {
            out.append("/>\n");
        } else {
            out.append(">");
            out.append(element.getValue());
            out.append(String.format("</%s>\n", nodeName));
        }
        return out;
    }
}