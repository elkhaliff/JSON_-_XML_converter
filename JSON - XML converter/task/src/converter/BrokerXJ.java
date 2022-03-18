package converter;

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
        Element element = convertMethod.getRoot();
        boolean addRoot = element.getSubMap().size() > 1;
        if (dataType == Converter.dtJSON) {
            if (addRoot) sb.append("<root>");
        } else sb.append("{\n");
        sb.append(element);
        if (dataType == Converter.dtJSON) {
            if (addRoot) sb.append("\n</root>");
        } else sb.append("\n}");
        return sb.toString();
    }

    public void print() {
        System.out.println(this);
    }

    public void setMethod() {
        if (ConvertXML.isXml(input)) {
            convertMethod = new ConvertXML();
            dataType = Converter.dtXML;
        } else if (ConvertJSON.isJson(input)) {
            convertMethod = new ConvertJSON();
            dataType = Converter.dtJSON;
        }
    }
}