package nro.utils;

import org.apache.log4j.Logger;

/**
 *
 * Build Arriety
 *
 */
public class Log {

    private static final Logger logger = Logger.getLogger(Log.class);

    /**
     * Note: System.out.print
     *
     * @param text
     */
    public static void log(String text) {
        logger.debug(text);
    }

    /**
     * Note: System.out.print
     *
     * @param text
     */
    public static void success(String text) {
        System.out.println(text);
    }

    /**
     * Note: System.out.print
     *
     * @param text
     */
    public static void warning(String text) {
        logger.warn(text);
    }

    /**
     * Note: System.out.print
     *
     * @param text
     */
    public static void error(String text) {
        logger.error(text);
    }

    public static void error(Class clazz, Exception ex, String logs) {
        logger.error(clazz.getName() + ":" + logs, ex);
    }

    public static void error(Class clazz, Exception ex) {
        logger.error(clazz.getName() + ": " + ex.getMessage(), ex);
    }

    public static void LogError(Class clazz, String status) {
        System.out.println("[" + clazz.getName() + "] " + status);
    }

}
