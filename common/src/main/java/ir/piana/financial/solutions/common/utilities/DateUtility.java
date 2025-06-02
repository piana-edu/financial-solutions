package ir.piana.financial.solutions.common.utilities;

import com.ghasemkiani.util.icu.PersianCalendar;
import ir.piana.financial.solutions.common.tools.Stringer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtility {
    private static final Logger log = LoggerFactory.getLogger(DateUtility.class);

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat SIMPLE_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a");

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(new Date().getTime());
    }

    public static java.sql.Date getCurrentDate() {
        return new java.sql.Date(new Date().getTime());
    }

    public static Date jalaliToGregorian(String inputDate) throws Exception {
        try {
            if (inputDate == null)
                return null;
            PersianCalendar persianCalendar = new PersianCalendar();
            GregorianCalendar gregorianCalendar = new GregorianCalendar();

            String[] inputDateParts = null;
            if (inputDate.contains("/")) {
                inputDateParts = inputDate.split("/");
            }
            if (inputDate.contains("-")) {
                inputDateParts = inputDate.split("-");
            }
            if (inputDateParts != null && inputDateParts.length == 3) {

                persianCalendar.set(Integer.parseInt(inputDateParts[0].trim()),
                        Integer.parseInt(inputDateParts[1].trim()) - 1, Integer.parseInt(inputDateParts[2].trim()));
                gregorianCalendar.setTime(persianCalendar.getTime());
                Date outputDate = new Date(gregorianCalendar.getTimeInMillis());

                return outputDate;
            } else
                throw new Exception("Incorrect date format");
        } catch (Exception e) {
            throw e;
        }

    }

    public static String gregorianToJalali(Date inputDate) {
        try {
            if (inputDate == null)
                return null;
            PersianCalendar persianCalendar = new PersianCalendar();
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(inputDate);
            persianCalendar.setTime(gregorianCalendar.getTime());
            String outputDate = persianCalendar.get(1) + "/"
                    + String.format("%2s", (persianCalendar.get(2) + 1)).replace(' ', '0') + "/"
                    + String.format("%2s", persianCalendar.get(0)).replace(' ', '0') + " "
                    + String.format("%2s", persianCalendar.get(11)).replace(' ', '0') + ":"
                    + String.format("%2s", persianCalendar.get(12)).replace(' ', '0') + ":"
                    + String.format("%2s", persianCalendar.get(13)).replace(' ', '0');
            return outputDate.trim();

        } catch (Exception e) {
            log.error(Stringer.of("gregorianToJalali", "Date:'", SIMPLE_DATE_FORMAT.format(inputDate),
                    "', Message:'", e.getMessage(), "'").toString());
            return null;
        }
    }

    public static String gregorianToJalali(Timestamp inputDate) {
        try {
            if (inputDate == null)
                return null;
            PersianCalendar persianCalendar = new PersianCalendar();
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(inputDate);
            persianCalendar.setTime(gregorianCalendar.getTime());
            String outputDate = String.format("%2s", persianCalendar.get(11)).replace(' ', '0') + ":"
                    + String.format("%2s", persianCalendar.get(12)).replace(' ', '0') + ":"
                    + String.format("%2s", persianCalendar.get(13)).replace(' ', '0') + " "
                    + persianCalendar.get(1) + "/"
                    + String.format("%2s", (persianCalendar.get(2) + 1)).replace(' ', '0') + "/"
                    + String.format("%2s", persianCalendar.get(5)).replace(' ', '0');
            return outputDate.trim();
        } catch (Exception e) {
            log.error(Stringer.of("gregorianToJalali => ", "Date:'",
                    SIMPLE_DATE_FORMAT.format(inputDate), "'", "Message:'", e.getMessage(), "'").toString());
            return null;
        }
    }

    public static String getJalaliDate(String strDate) {
        String jalali = "";
        try {
            Date parsedDate = SIMPLE_DATE_TIME_FORMAT.parse(strDate);
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            jalali = gregorianToJalali(timestamp);
        } catch (Exception e) {
            log.error(Stringer.concat("getJalaliDate => ", "Date:'" + strDate + "'",
                    "Message:'", e.getMessage(), "'"));
            return jalali;
        }
        return jalali;
    }

    public static String getJalaliDate(Timestamp inputDate) {
        try {
            if (inputDate == null)
                return null;
            PersianCalendar persianCalendar = new PersianCalendar();
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(inputDate);
            persianCalendar.setTime(gregorianCalendar.getTime());
            String outputDate = persianCalendar.get(1) + "/"
                    + String.format("%2s", (persianCalendar.get(2) + 1)).replace(' ', '0') + "/"
                    + String.format("%2s", persianCalendar.get(5)).replace(' ', '0');

            return outputDate.trim();

        } catch (Exception e) {
            log.error(Stringer.concat("getJalaliDate => ", "Date:'", SIMPLE_DATE_FORMAT.format(inputDate),
                    "'", "Message:'", e.getMessage(), "'"));
            return null;
        }
    }

    public static String getTime(Timestamp inputDate) {
        Date date = new Date(inputDate.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        return sdf.format(date);
    }
}
