package converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertXML extends Converter {

    @Override
    protected void parser(String input) {
        boolean isAttributes = false;
        String parameter = "";
        StringBuilder sb = new StringBuilder();
        int currPos = 0;

        while (currPos < input.length()) {
            switch (input.charAt(currPos)) {
                case '<' : {
                    if (parsType == typeParam) {
                        parameter = sb.toString();
                        sb = new StringBuilder();
                    }
                    parsType = typeKey;
                    currPos++;
                    break;
                }
                case '"' : {
                    String curStr = input.substring(currPos);
                    Pattern pattern = Pattern.compile("\"[.,\\w]*\""); //, Pattern.CASE_INSENSITIVE
                    Matcher matcher = pattern.matcher(curStr);
                    if (matcher.find()) {
                        currPos += matcher.end();
                        String word = matcher.group().replaceAll("\"", "");
                        if (parsType == typeValue) {
                            setData4LastKey(word, "@");
                            isAttributes = true;
                            parsType = typeKey;
                        }
                    }
                    break;
                }
                case '='  : { parsType = typeValue; currPos++; break; }
                case '/'  : { parsType = typeEnd; currPos++; break; }
                case '>'  :
                case ' '  : {
                    if (parsType == typeParam) {
                        sb.append(input.charAt(currPos));
                    } else {
                        if (parsType == typeKey) {
                            sb = saveKeyAndClearSB(sb);
                        } else if (parsType == typeValue) {
                            sb = saveValueAndClearSB(sb);
                        } else if (parsType == typeEnd) {
                            if (isAttributes && keys.size() > 0) {
                                setAttributes(parameter);
                                parameter = "";
                                isAttributes = false;
                                sb = new StringBuilder();
                            } else {
                                setData4LastKey(parameter);
                            }
                            parsType = typeEmpty;
                        } else if (parsType == typeParam) {
                            sb.append(input.charAt(currPos));
                        }
                        if (input.charAt(currPos) == '>') parsType = typeParam;
                    }
                    currPos++;
                    break;
                }
                default : {
                    sb.append(input.charAt(currPos));
                    currPos++;
                    break;
                }
            }
        }
    }

}
