package converter;

public class BrokerXJ {
    String input;
    Converter convertMethod;
    Converter printMethod;

    public BrokerXJ(String input) {
        this.input = input.trim();
        setMethod();
        this.convertMethod.parsData(input);
    }

    @Override
    public String toString() {
        return printMethod.print(convertMethod.getRoot());
    }

    public void setMethod() {
        Converter[] converters = new Converter[] {
                new ConvertXML(),
                new ConvertJSON()
        };

        if (converters[0].check(input)) {
            convertMethod = converters[0];
            printMethod = converters[1];
        } else {
            convertMethod = converters[1];
            printMethod = converters[0];
        }
    }
}