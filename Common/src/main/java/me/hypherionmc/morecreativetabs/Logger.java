package me.hypherionmc.morecreativetabs;

public class Logger {

    public static void error(String message) {
        ModConstants.logger.error("[MoreCreativeTabs] " + message);
    }

    public static void info(String message) {
        ModConstants.logger.info("[MoreCreativeTabs] " + message);
    }

    public static void warn(String message) {
        ModConstants.logger.warn("[MoreCreativeTabs] " + message);
    }

}
