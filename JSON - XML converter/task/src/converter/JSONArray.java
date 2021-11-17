package converter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class JSONArray implements Iterable<Object> {
    private final ArrayList<Object> myArrayList;

    public JSONArray() {
        this.myArrayList = new ArrayList<>();
    }

    public JSONArray(JSONTokener x) throws JSONException {
        this();
        if (x.nextClean() != '[') {
            throw x.syntaxError("A JSONArray text must start with '['");
        }

        char nextChar = x.nextClean();
        if (nextChar == 0) {
            throw x.syntaxError("Expected a ',' or ']'");
        }
        if (nextChar != ']') {
            x.back();
            for (;;) {
                if (x.nextClean() == ',') {
                    x.back();
                    this.myArrayList.add(JSONObject.NULL);
                } else {
                    x.back();
                    this.myArrayList.add(x.nextValue());
                }
                switch (x.nextClean()) {
                    case ',':
                        nextChar = x.nextClean();
                        if (nextChar == 0) {
                            throw x.syntaxError("Expected a ',' or ']'");
                        }
                        if (nextChar == ']') {
                            return;
                        }
                        x.back();
                        break;
                    case ']':
                        return;
                    default:
                        throw x.syntaxError("Expected a ',' or ']'");
                }
            }
        }
    }

    public JSONArray(Collection<?> collection) {
        if (collection == null) {
            this.myArrayList = new ArrayList<>();
        } else {
            this.myArrayList = new ArrayList<>(collection.size());
            this.addAll(collection);
        }
    }

    public JSONArray(Object array) throws JSONException {
        this();
        if (!array.getClass().isArray()) {
            throw new JSONException(
                    "JSONArray initial value should be a string or collection or array.");
        }
        this.addAll(array);
    }

    @Override
    public Iterator<Object> iterator() {
        return this.myArrayList.iterator();
    }

    public int length() {
        return this.myArrayList.size();
    }

    public Object opt(int index) {
        return (index < 0 || index >= this.length()) ? null : this.myArrayList
                .get(index);
    }

    public JSONArray put(Object value) {
        JSONObject.testValidity(value);
        this.myArrayList.add(value);
        return this;
    }

    public boolean similar(Object other) {
        if (!(other instanceof JSONArray)) {
            return true;
        }
        int len = this.length();
        if (len != ((JSONArray)other).length()) {
            return true;
        }
        for (int i = 0; i < len; i += 1) {
            Object valueThis = this.myArrayList.get(i);
            Object valueOther = ((JSONArray)other).myArrayList.get(i);
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
                if (JSONObject.isNumberSimilar((Number) valueThis, (Number) valueOther)) {
                    return true;
                }
            } else if (!valueThis.equals(valueOther)) {
                return true;
            }
        }
        return false;
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
        StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            return this.write(sw, indentFactor, 0).toString();
        }
    }

    public Writer write(Writer writer, int indentFactor, int indent) throws JSONException {
        try {
            boolean needsComma = false;
            int length = this.length();
            writer.write('[');

            if (length == 1) {
                try {
                    JSONObject.writeValue(writer, this.myArrayList.get(0),
                            indentFactor, indent);
                } catch (Exception e) {
                    throw new JSONException("Unable to write JSONArray value at index: 0", e);
                }
            } else if (length != 0) {
                final int newIndent = indent + indentFactor;

                for (int i = 0; i < length; i += 1) {
                    if (needsComma) {
                        writer.write(',');
                    }
                    if (indentFactor > 0) {
                        writer.write('\n');
                    }
                    JSONObject.indent(writer, newIndent);
                    try {
                        JSONObject.writeValue(writer, this.myArrayList.get(i),
                                indentFactor, newIndent);
                    } catch (Exception e) {
                        throw new JSONException("Unable to write JSONArray value at index: " + i, e);
                    }
                    needsComma = true;
                }
                if (indentFactor > 0) {
                    writer.write('\n');
                }
                JSONObject.indent(writer, indent);
            }
            writer.write(']');
            return writer;
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }

    public List<Object> toList() {
        List<Object> results = new ArrayList<>(this.myArrayList.size());
        for (Object element : this.myArrayList) {
            if (element == null || JSONObject.NULL.equals(element)) {
                results.add(null);
            } else if (element instanceof JSONArray) {
                results.add(((JSONArray) element).toList());
            } else if (element instanceof JSONObject) {
                results.add(((JSONObject) element).toMap());
            } else {
                results.add(element);
            }
        }
        return results;
    }

    private void addAll(Collection<?> collection) {
        this.myArrayList.ensureCapacity(this.myArrayList.size() + collection.size());
        for (Object o: collection){
            this.put(JSONObject.wrap(o));
        }
    }

    private void addAll(Iterable<?> iter) {
        for (Object o: iter){
            this.put(JSONObject.wrap(o));
        }
    }

    private void addAll(Object array) throws JSONException {
        if (array.getClass().isArray()) {
            int length = Array.getLength(array);
            this.myArrayList.ensureCapacity(this.myArrayList.size() + length);
            for (int i = 0; i < length; i += 1) {
                this.put(JSONObject.wrap(Array.get(array, i)));
            }
        } else if (array instanceof JSONArray) {
            this.myArrayList.addAll(((JSONArray)array).myArrayList);
        } else if (array instanceof Collection) {
            this.addAll((Collection<?>)array);
        } else if (array instanceof Iterable) {
            this.addAll((Iterable<?>)array);
        } else {
            throw new JSONException(
                    "JSONArray initial value should be a string or collection or array.");
        }
    }

}