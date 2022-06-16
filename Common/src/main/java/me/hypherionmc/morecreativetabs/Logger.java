package me.hypherionmc.morecreativetabs;

@Deprecated(forRemoval = true)
public class Logger {

    public static void error(String message) {
        ModConstants.logger.error(message);
    }

    public static void info(String message) {
        ModConstants.logger.info(message);
    }

    public static void warn(String message) {
        ModConstants.logger.warn(message);
    }

}
