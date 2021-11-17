package converter;


import java.util.Collections;
import java.util.Map;

public class XMLParserConfiguration {
    /** Original Configuration of the XML Parser. */
    public static final XMLParserConfiguration ORIGINAL
            = new XMLParserConfiguration();
    /** Original configuration of the XML Parser except that values are kept as strings. */
    public static final XMLParserConfiguration KEEP_STRINGS
            = new XMLParserConfiguration().withKeepStrings(true);

    /**
     * When parsing the XML into JSON, specifies if values should be kept as strings (<code>true</code>), or if
     * they should try to be guessed into JSON values (numeric, boolean, string)
     */
    private boolean keepStrings;

    /**
     * The name of the key in a JSON Object that indicates a CDATA section. Historically this has
     * been the value "content" but can be changed. Use <code>null</code> to indicate no CDATA
     * processing.
     */
    private String cDataTagName;

    /**
     * When parsing the XML into JSON, specifies if values with attribute xsi:nil="true"
     * should be kept as attribute(<code>false</code>), or they should be converted to
     * <code>null</code>(<code>true</code>)
     */
    private boolean convertNilAttributeToNull;

    /**
     * This will allow type conversion for values in XML if xsi:type attribute is defined
     */
    private Map<String, XMLXsiTypeConverter<?>> xsiTypeMap;

    /**
     * Default parser configuration. Does not keep strings (tries to implicitly convert
     * values), and the CDATA Tag Name is "content".
     */
    public XMLParserConfiguration () {
        this.keepStrings = false;
        this.cDataTagName = "content";
        this.convertNilAttributeToNull = false;
        this.xsiTypeMap = Collections.emptyMap();
    }

    private XMLParserConfiguration (final boolean keepStrings, final String cDataTagName,
                                    final boolean convertNilAttributeToNull, final Map<String, XMLXsiTypeConverter<?>> xsiTypeMap ) {
        this.keepStrings = keepStrings;
        this.cDataTagName = cDataTagName;
        this.convertNilAttributeToNull = convertNilAttributeToNull;
        this.xsiTypeMap = Collections.unmodifiableMap(xsiTypeMap);
    }

    @Override
    protected XMLParserConfiguration clone() {
        return new XMLParserConfiguration(
                this.keepStrings,
                this.cDataTagName,
                this.convertNilAttributeToNull,
                this.xsiTypeMap
        );
    }

    public boolean isKeepStrings() {
        return this.keepStrings;
    }

    public XMLParserConfiguration withKeepStrings(final boolean newVal) {
        XMLParserConfiguration newConfig = this.clone();
        newConfig.keepStrings = newVal;
        return newConfig;
    }

    public String getcDataTagName() {
        return this.cDataTagName;
    }

    public boolean isConvertNilAttributeToNull() {
        return this.convertNilAttributeToNull;
    }

    public XMLParserConfiguration withConvertNilAttributeToNull(final boolean newVal) {
        XMLParserConfiguration newConfig = this.clone();
        newConfig.convertNilAttributeToNull = newVal;
        return newConfig;
    }

    public Map<String, XMLXsiTypeConverter<?>> getXsiTypeMap() {
        return this.xsiTypeMap;
    }

}