package ir.piana.financial.solutions.common.tools;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Stringer {
    public static StringBuilder empty() {
        return new StringBuilder();
    }

    public static StringBuilder of(String string) {
        return new StringBuilder(string);
    }

    public static StringBuilder of(String string, String... strings) {
        return new StringBuilder(Stream.of(strings).collect(Collectors.joining()));
    }

    public static String concat(String... strings) {
        return String.join("", strings);
    }
}
