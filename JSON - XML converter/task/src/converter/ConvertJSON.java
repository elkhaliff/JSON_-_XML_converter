package converter;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertJSON extends Converter {

    @Override
    protected void parser(String input) {
        StringBuilder sb = new StringBuilder();
        int currPos = 0;
        clear();

        while (currPos < input.length()) {
            switch (input.charAt(currPos)) {
                case '"': {
                    String curStr = input.substring(currPos);
                    Pattern pattern = Pattern.compile("\"[@ #.,\\w]*\""); //, Pattern.CASE_INSENSITIVE
                    Matcher matcher = pattern.matcher(curStr);
                    if (matcher.find()) {
                        currPos += matcher.end();
                        String word = matcher.group().replaceAll("\"", "");
                        if (parsType == typeKey || parsType == typeEmpty) {
                            keys.add(word);
                            clear();
                        } else if (parsType == typeValue) {
                            setData4LastKey(word);
                            clear();
                        }
                    }
                    break;
                }
                case ',': {
                    if (parsType == typeValue) {
                        sb = saveValueAndClearSB(sb);
                    }
                    parsType = typeKey;
                    currPos++;
                    break; }
                case ':': { parsType = typeValue; currPos++; break; }
                case '{': { parsType = typeKey; currPos++; break; }
                case '}': {
                    if (parsType == typeValue) {
                        sb = saveValueAndClearSB(sb);
                    }
                    Map<String, Object> out = new LinkedHashMap<>(branch);
                    setData4LastKey(out);
                    branch.clear();
                    currPos++;
                    break;
                }
                case ' ':
                case '\n': { currPos++; break; }
                default: {
                    if (parsType == typeValue) {
                        sb.append(input.charAt(currPos));
                        currPos++;
                        break;
                    }
                }
            }
        }
    }
}