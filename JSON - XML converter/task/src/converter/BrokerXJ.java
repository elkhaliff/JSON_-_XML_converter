package converter;

import java.util.*;

public class BrokerXJ {
    final int dtXML = 0;
    final int dtJSON = 1;
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
        for (Element elm: elements) {
            sb.append(elm);
        }
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
