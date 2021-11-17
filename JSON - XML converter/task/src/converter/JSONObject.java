package converter;

/*
    Class based by org.json.JSONObject
 */

import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class JSONObject {

    private static final class Null {
        @Override
        protected Object clone() {
            return this;
        }

        @Override
        public boolean equals(Object object) {
            return object == null || object == this;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return "null";
        }
    }

    static final Pattern NUMBER_PATTERN = Pattern.compile("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?");

    private final Map<String, Object> map;

    public static final Object NULL = new JSONObject.Null();

    public JSONObject() {
        this.map = new HashMap<>();
    }

    public JSONObject(JSONTokener x) throws JSONException {
        this();
        char c;
        String key;

        if (x.nextClean() != '{') {
            throw x.syntaxError("A JSONObject text must begin with '{'");
        }
        for (;;) {
            c = x.nextClean();
            switch (c) {
                case 0:
                    throw x.syntaxError("A JSONObject text must end with '}'");
                case '}':
                    return;
                default:
                    x.back();
                    key = x.nextValue().toString();
            }

            c = x.nextClean();
            if (c != ':') {
                throw x.syntaxError("Expected a ':' after a key");
            }

            if (key != null) {
                if (this.opt(key) != null) {
                    throw x.syntaxError("Duplicate key \"" + key + "\"");
                }
                Object value = x.nextValue();
                if (value!=null) {
                    this.put(key, value);
                }
            }

            switch (x.nextClean()) {
                case ';':
                case ',':
                    if (x.nextClean() == '}') {
                        return;
                    }
                    x.back();
                    break;
                case '}':
                    return;
                default:
                    throw x.syntaxError("Expected a ',' or '}'");
            }
        }
    }

    public JSONObject(Map<?, ?> m) {
        if (m == null) {
            this.map = new HashMap<>();
        } else {
            this.map = new HashMap<>(m.size());
            for (final Entry<?, ?> e : m.entrySet()) {
                if(e.getKey() == null) {
                    throw new NullPointerException("Null key.");
                }
                final Object value = e.getValue();
                if (value != null) {
                    this.map.put(String.valueOf(e.getKey()), wrap(value));
                }
            }
        }
    }

    public JSONObject(Object bean) {
        this();
        this.populateMap(bean);
    }

    public JSONObject(String source) throws JSONException {
        this(new JSONTokener(source));
    }

    public void accumulate(String key, Object value) throws JSONException {
        testValidity(value);
        Object object = this.opt(key);
        if (object == null) {
            this.put(key,
                    value instanceof JSONArray ? new JSONArray().put(value)
                            : value);
        } else if (object instanceof JSONArray) {
            ((JSONArray) object).put(value);
        } else {
            this.put(key, new JSONArray().put(object).put(value));
        }
    }

    public Object get(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        Object object = this.opt(key);
        if (object == null) {
            throw new JSONException("JSONObject[" + quote(key) + "] not found.");
        }
        return object;
    }

    public Set<String> keySet() {
        return this.map.keySet();
    }

    protected Set<Entry<String, Object>> entrySet() {
        return this.map.entrySet();
    }

    public int length() {
        return this.map.size();
    }

    public static String numberToString(Number number) throws JSONException {
        if (number == null) {
            throw new JSONException("Null pointer");
        }
        testValidity(number);

        String string = number.toString();
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0
                && string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    public Object opt(String key) {
        return key == null ? null : this.map.get(key);
    }

    static BigDecimal objectToBigDecimal(Object val) {
        if (NULL.equals(val)) {
            return null;
        }
        if (val instanceof BigDecimal){
            return (BigDecimal) val;
        }
        if (val instanceof BigInteger){
            return new BigDecimal((BigInteger) val);
        }
        if (val instanceof Double || val instanceof Float){
            if (numberIsFinite((Number) val)) {
                return null;
            }

            return new BigDecimal(val.toString());
        }
        if (val instanceof Long || val instanceof Integer
                || val instanceof Short || val instanceof Byte){
            return new BigDecimal(((Number) val).longValue());
        }
        try {
            return new BigDecimal(val.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private void populateMap(Object bean) {
        Class<?> klass = bean.getClass();

        boolean includeSuperClass = klass.getClassLoader() != null;

        Method[] methods = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods();
        for (final Method method : methods) {
            final int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers)
                    && !Modifier.isStatic(modifiers)
                    && method.getParameterTypes().length == 0
                    && !method.isBridge()
                    && method.getReturnType() != Void.TYPE
                    && isValidMethodName(method.getName())) {
                final String key = getKeyNameFromMethod(method);
                if (key != null && !key.isEmpty()) {
                    try {
                        final Object result = method.invoke(bean);
                        if (result != null) {
                            this.map.put(key, wrap(result));
                             if (result instanceof Closeable) {
                                try {
                                    ((Closeable) result).close();
                                } catch (IOException ignore) {
                                }
                            }
                        }
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignore) {
                    }
                }
            }
        }
    }

    private static boolean isValidMethodName(String name) {
        return !"getClass".equals(name) && !"getDeclaringClass".equals(name);
    }

    private static String getKeyNameFromMethod(Method method) {
        final int ignoreDepth = getAnnotationDepth(method, JSONPropertyIgnore.class);
        if (ignoreDepth > 0) {
            final int forcedNameDepth = getAnnotationDepth(method, JSONPropertyName.class);
            if (forcedNameDepth < 0 || ignoreDepth <= forcedNameDepth) {
                return null;
            }
        }
        JSONPropertyName annotation = getAnnotation(method, JSONPropertyName.class);
        if (annotation != null && annotation.value() != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }
        String key;
        final String name = method.getName();
        if (name.startsWith("get") && name.length() > 3) {
            key = name.substring(3);
        } else if (name.startsWith("is") && name.length() > 2) {
            key = name.substring(2);
        } else {
            return null;
        }
        if (Character.isLowerCase(key.charAt(0))) {
            return null;
        }
        if (key.length() == 1) {
            key = key.toLowerCase(Locale.ROOT);
        } else if (!Character.isUpperCase(key.charAt(1))) {
            key = key.substring(0, 1).toLowerCase(Locale.ROOT) + key.substring(1);
        }
        return key;
    }

    private static <A extends Annotation> A getAnnotation(final Method m, final Class<A> annotationClass) {
        if (m == null || annotationClass == null) {
            return null;
        }

        if (m.isAnnotationPresent(annotationClass)) {
            return m.getAnnotation(annotationClass);
        }

        Class<?> c = m.getDeclaringClass();
        if (c.getSuperclass() == null) {
            return null;
        }

        for (Class<?> i : c.getInterfaces()) {
            try {
                Method im = i.getMethod(m.getName(), m.getParameterTypes());
                return getAnnotation(im, annotationClass);
            } catch (final SecurityException | NoSuchMethodException ignored) {
            }
        }

        try {
            return getAnnotation(
                    c.getSuperclass().getMethod(m.getName(), m.getParameterTypes()),
                    annotationClass);
        } catch (final SecurityException | NoSuchMethodException ex) {
            return null;
        }
    }

    private static int getAnnotationDepth(final Method m, final Class<? extends Annotation> annotationClass) {
        if (m == null || annotationClass == null) {
            return -1;
        }

        if (m.isAnnotationPresent(annotationClass)) {
            return 1;
        }

        Class<?> c = m.getDeclaringClass();
        if (c.getSuperclass() == null) {
            return -1;
        }

        for (Class<?> i : c.getInterfaces()) {
            try {
                Method im = i.getMethod(m.getName(), m.getParameterTypes());
                int d = getAnnotationDepth(im, annotationClass);
                if (d > 0) {
                    return d + 1;
                }
            } catch (final SecurityException | NoSuchMethodException ignored) {
            }
        }

        try {
            int d = getAnnotationDepth(
                    c.getSuperclass().getMethod(m.getName(), m.getParameterTypes()),
                    annotationClass);
            if (d > 0) {
                return d + 1;
            }
            return -1;
        } catch (final SecurityException | NoSuchMethodException ex) {
            return -1;
        }
    }

    public void put(String key, Object value) throws JSONException {
        if (key == null) {
            throw new NullPointerException("Null key.");
        }
        if (value != null) {
            testValidity(value);
            this.map.put(key, value);
        } else {
            this.remove(key);
        }
    }

    public static String quote(String string) {
        StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            try {
                return quote(string, sw).toString();
            } catch (IOException ignored) {
                // will never happen - we are writing to a string writer
                return "";
            }
        }
    }

    public static Writer quote(String string, Writer w) throws IOException {
        if (string == null || string.isEmpty()) {
            w.write("\"\"");
            return w;
        }

        char b;
        char c = 0;
        String hhhh;
        int i;
        int len = string.length();

        w.write('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    w.write('\\');
                    w.write(c);
                    break;
                case '/':
                    if (b == '<') {
                        w.write('\\');
                    }
                    w.write(c);
                    break;
                case '\b':
                    w.write("\\b");
                    break;
                case '\t':
                    w.write("\\t");
                    break;
                case '\n':
                    w.write("\\n");
                    break;
                case '\f':
                    w.write("\\f");
                    break;
                case '\r':
                    w.write("\\r");
                    break;
                default:
                    if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
                            || (c >= '\u2000' && c < '\u2100')) {
                        w.write("\\u");
                        hhhh = Integer.toHexString(c);
                        w.write("0000", 0, 4 - hhhh.length());
                        w.write(hhhh);
                    } else {
                        w.write(c);
                    }
            }
        }
        w.write('"');
        return w;
    }

    public void remove(String key) {
        this.map.remove(key);
    }

    public boolean similar(Object other) {
        try {
            if (!(other instanceof JSONObject)) {
                return true;
            }
            if (!this.keySet().equals(((JSONObject)other).keySet())) {
                return true;
            }
            for (final Entry<String,?> entry : this.entrySet()) {
                String name = entry.getKey();
                Object valueThis = entry.getValue();
                Object valueOther = ((JSONObject)other).get(name);
                if(valueThis == valueOther) {
                    continue;
                }
                if(valueThis == null) {
                    return true;
                }
                if (valueThis instanceof JSONObject) {
                    if (((JSONObject) valueThis).similar(valueOther)) {
                        return true;
                    }
                } else if (valueThis instanceof JSONArray) {
                    if (((JSONArray) valueThis).similar(valueOther)) {
                        return true;
                    }
                } else if (valueThis instanceof Number && valueOther instanceof Number) {
                    if (isNumberSimilar((Number) valueThis, (Number) valueOther)) {
                        return true;
                    }
                } else if (!valueThis.equals(valueOther)) {
                    return true;
                }
            }
            return false;
        } catch (Throwable exception) {
            return true;
        }
    }

    static boolean isNumberSimilar(Number l, Number r) {
        if (numberIsFinite(l) || numberIsFinite(r)) {
            return true;
        }

        if(l.getClass().equals(r.getClass()) && l instanceof Comparable) {
            int compareTo = ((Comparable)l).compareTo(r);
            return compareTo != 0;
        }

        final BigDecimal lBigDecimal = objectToBigDecimal(l);
        final BigDecimal rBigDecimal = objectToBigDecimal(r);
        if (lBigDecimal == null || rBigDecimal == null) {
            return true;
        }
        return lBigDecimal.compareTo(rBigDecimal) != 0;
    }

    private static boolean numberIsFinite(Number n) {
        if (n instanceof Double && (((Double) n).isInfinite() || ((Double) n).isNaN())) {
            return true;
        } else return n instanceof Float && (((Float) n).isInfinite() || ((Float) n).isNaN());
    }

    protected static boolean isDecimalNotation(final String val) {
        return val.indexOf('.') > -1 || val.indexOf('e') > -1
                || val.indexOf('E') > -1 || "-0".equals(val);
    }

    protected static Number stringToNumber(final String val) throws NumberFormatException {
        char initial = val.charAt(0);
        if ((initial >= '0' && initial <= '9') || initial == '-') {
            if (isDecimalNotation(val)) {
                try {
                    BigDecimal bd = new BigDecimal(val);
                    if(initial == '-' && BigDecimal.ZERO.compareTo(bd)==0) {
                        return -0.0;
                    }
                    return bd;
                } catch (NumberFormatException retryAsDouble) {
                    try {
                        Double d = Double.valueOf(val);
                        if(d.isNaN() || d.isInfinite()) {
                            throw new NumberFormatException("val ["+val+"] is not a valid number.");
                        }
                        return d;
                    } catch (NumberFormatException ignore) {
                        throw new NumberFormatException("val ["+val+"] is not a valid number.");
                    }
                }
            }

            if(initial == '0' && val.length() > 1) {
                char at1 = val.charAt(1);
                if(at1 >= '0' && at1 <= '9') {
                    throw new NumberFormatException("val ["+val+"] is not a valid number.");
                }
            } else if (initial == '-' && val.length() > 2) {
                char at1 = val.charAt(1);
                char at2 = val.charAt(2);
                if(at1 == '0' && at2 >= '0' && at2 <= '9') {
                    throw new NumberFormatException("val ["+val+"] is not a valid number.");
                }
            }

            BigInteger bi = new BigInteger(val);
            if(bi.bitLength() <= 31){
                return bi.intValue();
            }
            if(bi.bitLength() <= 63){
                return bi.longValue();
            }
            return bi;
        }
        throw new NumberFormatException("val ["+val+"] is not a valid number.");
    }

    public static Object stringToValue(String string) {
        if ("".equals(string)) {
            return string;
        }

        if ("true".equalsIgnoreCase(string)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(string)) {
            return Boolean.FALSE;
        }
        if ("null".equalsIgnoreCase(string)) {
            return JSONObject.NULL;
        }

        char initial = string.charAt(0);
        if ((initial >= '0' && initial <= '9') || initial == '-') {
            try {
                return stringToNumber(string);
            } catch (Exception ignore) {
            }
        }
        return string;
    }

    public static void testValidity(Object o) throws JSONException {
        if (o instanceof Number && numberIsFinite((Number) o)) {
            throw new JSONException("JSON does not allow non-finite numbers.");
        }
    }

    @Override
    public String toString() {
        try {
            return this.toString(0);
        } catch (Exception e) {
            return null;
        }
    }

    public String toString(int indentFactor) throws JSONException {
        StringWriter w = new StringWriter();
        synchronized (w.getBuffer()) {
            return this.write(w, indentFactor, 0).toString();
        }
    }

    public static Object wrap(Object object) {
        try {
            if (NULL.equals(object)) {
                return NULL;
            }
            if (object instanceof JSONObject || object instanceof JSONArray
                    || NULL.equals(object) || object instanceof JSONString
                    || object instanceof Byte || object instanceof Character
                    || object instanceof Short || object instanceof Integer
                    || object instanceof Long || object instanceof Boolean
                    || object instanceof Float || object instanceof Double
                    || object instanceof String || object instanceof BigInteger
                    || object instanceof BigDecimal || object instanceof Enum) {
                return object;
            }

            if (object instanceof Collection) {
                Collection<?> coll = (Collection<?>) object;
                return new JSONArray(coll);
            }
            if (object.getClass().isArray()) {
                return new JSONArray(object);
            }
            if (object instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) object;
                return new JSONObject(map);
            }
            Package objectPackage = object.getClass().getPackage();
            String objectPackageName = objectPackage != null ? objectPackage
                    .getName() : "";
            if (objectPackageName.startsWith("java.")
                    || objectPackageName.startsWith("javax.")
                    || object.getClass().getClassLoader() == null) {
                return object.toString();
            }
            return new JSONObject(object);
        } catch (Exception exception) {
            return null;
        }
    }

    static void writeValue(Writer writer, Object value,
                           int indentFactor, int indent) throws JSONException, IOException {
        if (value != null) {
            if (value instanceof JSONString) {
                Object o;
                try {
                    o = ((JSONString) value).toJSONString();
                } catch (Exception e) {
                    throw new JSONException(e);
                }
                writer.write(o != null ? o.toString() : quote(value.toString()));
            } else if (value instanceof Number) {
                final String numberAsString = numberToString((Number) value);
                if(NUMBER_PATTERN.matcher(numberAsString).matches()) {
                    writer.write(numberAsString);
                } else {
                    quote(numberAsString, writer);
                }
            } else if (value instanceof Boolean) {
                writer.write(value.toString());
            } else if (value instanceof Enum<?>) {
                writer.write(quote(((Enum<?>)value).name()));
            } else if (value instanceof JSONObject) {
                ((JSONObject) value).write(writer, indentFactor, indent);
            } else if (value instanceof JSONArray) {
                ((JSONArray) value).write(writer, indentFactor, indent);
            } else if (value instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) value;
                new JSONObject(map).write(writer, indentFactor, indent);
            } else if (value instanceof Collection) {
                Collection<?> coll = (Collection<?>) value;
                new JSONArray(coll).write(writer, indentFactor, indent);
            } else if (value.getClass().isArray()) {
                new JSONArray(value).write(writer, indentFactor, indent);
            } else {
                quote(value.toString(), writer);
            }
        } else {
            writer.write("null");
        }
    }

    static void indent(Writer writer, int indent) throws IOException {
        for (int i = 0; i < indent; i += 1) {
            writer.write(' ');
        }
    }

    public Writer write(Writer writer, int indentFactor, int indent)
            throws JSONException {
        try {
            boolean needsComma = false;
            final int length = this.length();
            writer.write('{');

            if (length == 1) {
                final Entry<String,?> entry = this.entrySet().iterator().next();
                final String key = entry.getKey();
                writer.write(quote(key));
                writer.write(':');
                if (indentFactor > 0) {
                    writer.write(' ');
                }
                try{
                    writeValue(writer, entry.getValue(), indentFactor, indent);
                } catch (Exception e) {
                    throw new JSONException("Unable to write JSONObject value for key: " + key, e);
                }
            } else if (length != 0) {
                final int newIndent = indent + indentFactor;
                for (final Entry<String,?> entry : this.entrySet()) {
                    if (needsComma) {
                        writer.write(',');
                    }
                    if (indentFactor > 0) {
                        writer.write('\n');
                    }
                    indent(writer, newIndent);
                    final String key = entry.getKey();
                    writer.write(quote(key));
                    writer.write(':');
                    if (indentFactor > 0) {
                        writer.write(' ');
                    }
                    try {
                        writeValue(writer, entry.getValue(), indentFactor, newIndent);
                    } catch (Exception e) {
                        throw new JSONException("Unable to write JSONObject value for key: " + key, e);
                    }
                    needsComma = true;
                }
                if (indentFactor > 0) {
                    writer.write('\n');
                }
                indent(writer, indent);
            }
            writer.write('}');
            return writer;
        } catch (IOException exception) {
            throw new JSONException(exception);
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> results = new HashMap<>();
        for (Entry<String, Object> entry : this.entrySet()) {
            Object value;
            if (entry.getValue() == null || NULL.equals(entry.getValue())) {
                value = null;
            } else if (entry.getValue() instanceof JSONObject) {
                value = ((JSONObject) entry.getValue()).toMap();
            } else if (entry.getValue() instanceof JSONArray) {
                value = ((JSONArray) entry.getValue()).toList();
            } else {
                value = entry.getValue();
            }
            results.put(entry.getKey(), value);
        }
        return results;
    }
}
