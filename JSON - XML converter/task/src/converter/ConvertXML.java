package converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertXML extends Converter {

    @Override
    protected void parser(String input) {
        String parameter = "";
        StringBuilder sb = new StringBuilder();
        int currPos = 0;

        while (currPos < input.length()) {
            switch (input.charAt(currPos)) {
                case '<' : {
                    if (parsType == typeValue) {
                        parameter = sb.toString().trim();
                        sb = new StringBuilder();
                    }
                    parsType = typeKey;
                    currPos++;
                    break;
                }
                case '"' : {
                    String curStr = input.substring(currPos);
                    Pattern pattern = Pattern.compile("\"[.,\\w]*\"");
                    Matcher matcher = pattern.matcher(curStr);
                    if (matcher.find()) {
                        currPos += matcher.end();
                        String word = matcher.group().replaceAll("\"", "");
                        if (parsType == typeAttributeValue) {
                            setAttribute(word);
                            parsType = typeAttributeKey;
                        }
                    }
                    break;
                }
                case '='  : {
                    if (parsType == typeAttributeKey) {
                        attributesKey = sb.toString().trim();
                        sb = new StringBuilder();
                    }
                    parsType = typeAttributeValue;
                    currPos++;
                    break;
                }
                case '/'  : { parsType = typeEnd; currPos++; break; }
                case '>'  :
                case ' '  : {
                    if (parsType == typeValue) {
                        sb.append(input.charAt(currPos));
                    } else {
                        if (parsType == typeKey) {
                            newElement(sb.toString().trim());
                            sb = new StringBuilder();
                            parsType = typeAttributeKey;
                        } else if (parsType == typeEnd) {
                            sb = new StringBuilder();
                            if (parameter.length() > 0) {
                                setValue(parameter);
                                parameter = "";
                            }
                            absorbSubElement();
                            parsType = typeEmpty;
                        } else if (parsType == typeValue) {
                            sb.append(input.charAt(currPos));
                        }
                        if (input.charAt(currPos) == '>' && parsType != typeEnd) parsType = typeValue;
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
