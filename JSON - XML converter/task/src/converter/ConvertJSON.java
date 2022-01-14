package converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertJSON extends Converter {

    //TODO: проверить почему схлопываются больше обьектов, чем нужно
    // проверить обработку обьектов после аттрибутов (пересчет атрибутов)

    private void setValueByType(String value) {
        if (parsType == typeValue) {
            setValue(value);
            absorbSubElement();
        } else if (parsType == typeParentValue) {
            setParentValue(value);
        } else if (parsType == typeAttributeValue) {
            setAttribute(value);
        }
        parsType = typeEmpty;
    }

    @Override
    protected void parser(String input) {
        StringBuilder sb = new StringBuilder();
        int currPos = 0;
        parsType = typeEmpty;

        while (currPos < input.length()) {
            switch (input.charAt(currPos)) {
                case '"': {
                    String curStr = input.substring(currPos);
                    Pattern pattern = Pattern.compile("\"[@ #.,\\w-']*\""); //, Pattern.CASE_INSENSITIVE
                    Matcher matcher = pattern.matcher(curStr);
                    if (matcher.find()) {
                        currPos += matcher.end();
                        String word = matcher.group().replaceAll("\"", "").trim();
                            if (parsType == typeKey || parsType == typeEmpty || parsType == typeEnd) {
                                if (word.length() > 0 && word.charAt(0) == '@') {
                                    keyAttributes = word.substring(1);
                                    parsType = typeAttributeKey;
                                } else if (word.length() > 0 && word.charAt(0) == '#') {
                                    parsType = typeParentKey;
                                } else {
                                    newElement(word);
                                }
                            } else if (parsType % 2 == 0) {
                                setValueByType(word);
                            }
                    }
                    break;
                }
                case ',': {
                    if (parsType % 2 == 0) { // for numbers or null - values without quotes
                        setValueByType(sb.toString().trim());
                        sb = new StringBuilder();
                        absorbSubElement(); // SUB value
                        parsType = typeEmpty;
                    } else if (parsType != typeAttributeValue && parsType != typeEmpty && parsType != typeEnd) {
                        absorbPointElement(); // Any Element
                        parsType = typeEmpty;
                    } else
                        parsType = typeKey;
                    currPos++;
                    break; }
                case ':': { if (parsType == typeKey || parsType == typeAttributeKey || parsType == typeParentKey)
                                parsType +=1;
                            currPos++;
                            break;
                    }
                case '{': { parsType = typeKey; currPos++; break; }
                case '}': {
                    if (parsType % 2 == 0) { // all values (for numbers or null - values without quotes )
                        setValueByType(sb.toString().trim());
                        sb = new StringBuilder();
                    }
                    absorbSubElement();
                    parsType = typeEnd;
                    currPos++;
                    break;
                }
                case ' ':
                default: {
                    if (parsType % 2 == 0) {
                        sb.append(input.charAt(currPos));
                    }
                    currPos++;
                    break;
                }
            }
        }
    }
}