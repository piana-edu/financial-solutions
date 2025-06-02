package ir.piana.financial.solutions.common.utilities;

import ir.piana.financial.solutions.common.tools.Stringer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtility {
    private static final Logger log = LoggerFactory.getLogger(StringUtility.class);

    public static String lPad(String value, char padCharacter, int length) {
        String paddedValue = value;
        try {
            String format = "%" + length + "s";
            paddedValue = String.format(format, value).replace(' ', padCharacter);
        } catch (Exception e) {
            log.error(Stringer.concat("lPad", "Value:", value, " PadCharacter:", String.valueOf(padCharacter),
                    " Length:", String.valueOf(length), "Message:'", e.getMessage(), "'"));
        }
        return paddedValue;
    }

    public static Boolean checkStringValidChar(String str) {
        Boolean res = false;
        String regex = "^[a-zA-Z0-9 ]*$";
        if (str.matches(regex)) {
            res = true;
        }
        return res;
    }
}
