package converter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Converter {

    private final Map<String, Object> tree = new LinkedHashMap<>();
    protected final Map<String, Object> branch = new LinkedHashMap<>();
    protected final List<String> keys = new ArrayList<>();

    final protected int typeEmpty = -1;
    final protected int typeKey = 0;
    final protected int typeValue = 1;
    final protected int typeEnd = 2;
    final protected int typeParam = 3;
    protected int parsType;

    public void clear() {
        parsType = typeEmpty;
    }

    protected void setData4LastKey(Object obj) {
        setData4LastKey(obj, null);
    }

    protected void setData4LastKey(Object obj, String prefix) {
        String pref = prefix == null ? "" : prefix;
        if (keys.size() > 0) {
            int lastInd = keys.size() - 1;
            if (lastInd > 0) {
                branch.put(pref + keys.get(lastInd), obj);
            } else
                tree.put(keys.get(lastInd), obj);
            keys.remove(lastInd);
        }
    }

    protected void setAttributes(String parameter) {
        int lastInd = keys.size() - 1;
        if (lastInd >= 0) {
            branch.put("#" + keys.get(lastInd), (parameter.length() == 0) ? "null" : parameter);
        }
        Map<String, Object> out = new LinkedHashMap<>(branch);
        setData4LastKey(out);
        branch.clear();
    }

    public Map<String, Object> getData(String input) {
        parser(input.trim());
        return tree;
    }

    protected StringBuilder saveValueAndClearSB(StringBuilder sb) {
        String tmp = sb.toString().trim();
        if (tmp.length() > 0) setData4LastKey(tmp);
        return new StringBuilder();
    }

    protected StringBuilder saveKeyAndClearSB(StringBuilder sb) {
        String tmp = sb.toString().trim();
        if (tmp.length() > 0) keys.add(tmp);
        return new StringBuilder();
    }

    protected abstract void parser(String input);
}