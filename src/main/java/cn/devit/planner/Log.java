package cn.devit.planner;

import org.slf4j.helpers.MessageFormatter;

public final class Log {

    public static void d(String format, Object... arguments) {
        System.out.println(
                MessageFormatter.arrayFormat(format, arguments).getMessage());
    }
}
