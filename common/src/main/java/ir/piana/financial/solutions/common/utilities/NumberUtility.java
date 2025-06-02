package ir.piana.financial.solutions.common.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;

public class NumberUtility {
    private static final Logger log = LoggerFactory.getLogger(NumberUtility.class);

    public static String numberToCommaSeperatedFormat(long number) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumIntegerDigits(1);
        numberFormat.setMaximumIntegerDigits(19);
        numberFormat.setGroupingUsed(true);
        return numberFormat.format(number);
    }
}
