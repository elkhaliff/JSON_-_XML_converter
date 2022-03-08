package converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertJSON extends Converter {

    //TODO: проверить почему схлопываются больше обьектов, чем нужно
    // проверить обработку обьектов после аттрибутов (пересчет атрибутов)
    // !!! при использовании кавычек - прасится почти всё
    // не парсятся null и цифры
    // не парсятся не правильные атрибуты - см. первый пример inner9 и особенно inner12 (в последнем идет замена неправильных)

    private void setValueByType(String value) {
        if (parsType == typeValue) {
            setValue(value);
            absorbSubElement();
        } else if (parsType == typeParentValue) {
            setParentValue(parentKey, value);
            parentKey = null;
        } else if (parsType == typeAttributeValue) {
            setAttribute(value);
        }
        parsType = typeEmpty;
    }

    @Override
    protected void parser(String input) {
        StringBuilder sbAccumulator = new StringBuilder();
        int currPos = 0;
        parsType = typeEmpty;

        while (currPos < input.length()) {
            switch (input.charAt(currPos)) {
                case 'n': { // null
                    String curStr = input.substring(currPos, currPos+4);
                    if (curStr.equals("null")){
                        setValueByType(curStr);
                        currPos += 4;
                        break;
                    }
                }
                case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':{
                    String curStr = input.substring(currPos);
                    Pattern pattern = Pattern.compile("[\\d. ]*[,}]"); //, Pattern.CASE_INSENSITIVE
                    Matcher matcher = pattern.matcher(curStr);
                    if (matcher.find()) {
                        String word = matcher.group().replaceAll("[,}]", "").trim();
                        setValueByType(word);
                        currPos += matcher.end() - 1;
                        break;
                    }
                }
                case '"': {
                    String curStr = input.substring(currPos);
                    Pattern pattern = Pattern.compile("\"[@ #.,\\w-']*\""); //, Pattern.CASE_INSENSITIVE
                    Matcher matcher = pattern.matcher(curStr);
                    if (matcher.find()) {
                        currPos += matcher.end();
                        String word = matcher.group().replaceAll("\"", "").trim();
                            if (parsType == typeKey || parsType == typeEmpty || parsType == typeEnd) {
                                if (word.length() > 0 && word.charAt(0) == '@') {
                                    attributesKey = word.substring(1);
                                    parsType = typeAttributeKey;
                                } else if (word.length() > 0 && word.charAt(0) == '#') {
                                    parentKey = word.substring(1);
                                    parsType = typeParentKey;
                                } else {
                                    newElement(word);
                                }
                            } else if (parsType % 2 == 0) { // value's type
                                setValueByType(word);
                            }
                    }
                    break;
                }
                case ',': {
                    if (parsType % 2 == 0) { // value's type
                        setValueByType(sbAccumulator.toString().trim());
                        sbAccumulator = new StringBuilder();
                        absorbSubElement(); // SUB value
                        parsType = typeEmpty;
                    } else if (parsType != typeAttributeValue && parsType != typeEmpty && parsType != typeEnd) {
                        absorbPointElement(); // Any Element
                        parsType = typeEmpty;
                    } else
                        parsType = typeKey;
                    currPos++;
                    break;
                }
                case ':': {
                    //if (parsType == typeKey || parsType == typeAttributeKey || parsType == typeParentKey)
                    if (parsType % 3 == 0) // key's type - typeKey, typeAttributeKey, typeParentKey
                        parsType += 1; // value's type
                    currPos++;
                    break;
                }
                case '{': { parsType = typeKey; currPos++; break; }
                case '}': {
                    if (parsType % 2 == 0) { // value's type  // (for numbers or null - values without quotes )
                        setValueByType(sbAccumulator.toString().trim());
                        sbAccumulator = new StringBuilder();
                    }
                    absorbSubElement();
                    parsType = typeEnd;
                    currPos++;
                    break;
                }
                case ' ':
                default: {
                    if (parsType % 2 == 0) { // value's type
                        sbAccumulator.append(input.charAt(currPos));
                    }
                    currPos++;
                    break;
                }
            }
        }
    }
}