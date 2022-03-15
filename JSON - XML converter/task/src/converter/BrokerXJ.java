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
        if (elements.size() > 1 && dataType == Converter.dtJSON)
            sb.append("<root>");
        for (Element elm: elements) {
            sb.append(elm.toString(dataType));
        }
        if (elements.size() > 1 && dataType == Converter.dtJSON)
            sb.append("\n</root>");
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
