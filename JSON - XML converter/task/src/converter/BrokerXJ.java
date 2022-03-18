package converter;

import java.util.*;

public class BrokerXJ {
    int dataType;
    String input;
    Converter convertMethod;

    public BrokerXJ(String input) {
        this.input = input.trim();
        setMethod();
    }

    public void getData() {
        this.convertMethod.getData(input);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<Element> elements = convertMethod.getElements();
        if (dataType == Converter.dtJSON) {
            if (elements.size() > 1)
                sb.append("<root>");
        } else
            sb.append("{");

        for (Element elm: elements) {
            sb.append(elm.toString());
        }

        if (dataType == Converter.dtJSON) {
            if (elements.size() > 1)
                sb.append("\n</root>");
        } else
            sb.append("\n}");
        return sb.toString();
    }

    public void print() {
        System.out.println(this);
    }

    public void setMethod() {
        if (input.charAt(0) == '<') {
            convertMethod = new ConvertXML();
            dataType = Converter.dtXML;
        } else {
            convertMethod = new ConvertJSON();
            dataType = Converter.dtJSON;
        }
    }
}
