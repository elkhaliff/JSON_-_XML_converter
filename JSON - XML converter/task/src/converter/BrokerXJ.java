package converter;

import java.util.*;

public class BrokerXJ {
    final int dtXML = 0;
    final int dtJSON = 1;
    int dataType;
    String input;
    Converter convertMethod;
    Map<String, Object> data;

    public BrokerXJ(String input) {
        this.input = input.trim();
        setMethod();
    }

    public void  getData() {
        this.data = this.convertMethod.getData(input);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<Element> elements = convertMethod.getElements();
        for (Element elm: elements) {
            sb.append(elm);
            sb.append("\n");
        }
        /*
        StringBuilder sb = new StringBuilder();
        for (var entry: data.entrySet()) {
            Object value = entry.getValue();
            switch (dataType) {
                case dtXML: {
                    if (value instanceof Map) {
                        Map<String, Object> tmp = (LinkedHashMap) value;
                        sb.append(String.format("{ \"%s\" : { ", entry.getKey()));
                        boolean quote = false;
                        for (var attrib: tmp.entrySet()) {
                            if (quote) sb.append(", ");
                            if (attrib.getValue().equals("null"))
                                sb.append(String.format("\"%s\" : null", attrib.getKey()));
                            else
                                sb.append(String.format("\"%s\" : \"%s\"", attrib.getKey(), attrib.getValue()));
                            if (!quote) quote = true;
                        }
                        sb.append("} }");
                    } else {
                        sb.append(String.format("{\"%s\" : %s}", entry.getKey(), entry.getValue() == null ? "null" :
                                String.format("\"%s\"", entry.getValue())));
                    }
                    break;
                }
                case dtJSON: {
                    if (value instanceof Map) {
                        Map<String, Object> tmp = (LinkedHashMap) value;
                        sb.append(String.format("<%s", entry.getKey()));
                        String property = "";
                        for (var attrib: tmp.entrySet()) {
                            if (attrib.getValue() instanceof String) {
                                String key = attrib.getKey();
                                String attribute = (String) attrib.getValue();
                                if (key.charAt(0) == '@') {
                                    sb.append(String.format(" %s = \"%s\"", key.substring(1), attribute));
                                } else if (key.charAt(0) == '#') {
                                    property = attribute;
                                } //else //if attribute is element
                            } //else //if value is Map
                        }
                        if (!Objects.equals(property, "null")) {
                            sb.append(String.format(">%s</%s>", property, entry.getKey()));
                        } else {
                            sb.append(" />");
                        }
                    } else {
                        if (!Objects.equals(value, "null")) {
                            sb.append(String.format("<%s>%s</%s>", entry.getKey(), value, entry.getKey()));
                        } else {
                            sb.append(String.format("<%s/>", entry.getKey()));
                        }
                    }
                    break;
                }
            }
        }
         */
        return sb.toString();
    }

    public void print() {
        System.out.println(this);
    }

    public void setMethod() {
        if (input.charAt(0) == '<') {
            convertMethod = new ConvertXML();
            dataType = dtXML;
        } else {
            convertMethod = new ConvertJSON();
            dataType = dtJSON;
        }
    }
}
