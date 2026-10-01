package ca.seneca.apd545.RXHgrandhotel.util;

import java.io.IOException;
import java.util.logging.*;


public class LoggerUtil {

    private static final Logger logger = Logger.getLogger("HotelSystemLogger");
    private static boolean initialized = false;


    public static synchronized void init() {
        if (initialized) return;

        try {

            FileHandler handler = new FileHandler(
                    "system_logs.%g.log", 1024 * 1024, 10, true);
            handler.setFormatter(new SimpleFormatter());

            logger.addHandler(handler);
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.ALL);

            initialized = true;
            logger.info("[LoggerUtil] Logger initialized successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }



    public static void info(String actor, String action,
                            String entityType, String entityId, String message) {
        logger.info(format(actor, action, entityType, entityId, message));
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void warn(String actor, String action,
                            String entityType, String entityId, String message) {
        logger.warning(format(actor, action, entityType, entityId, message));
    }

    public static void warn(String message) {
        logger.warning(message);
    }

    public static void severe(String actor, String action,
                              String entityType, String entityId,
                              String message, Throwable throwable) {
        logger.log(Level.SEVERE,
                format(actor, action, entityType, entityId, message), throwable);
    }

    public static void severe(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }

    public static void severe(String message) {
        logger.severe(message);
    }


    public static Logger getLogger() {
        return logger;
    }



    private static String format(String actor, String action,
                                 String entityType, String entityId, String message) {
        return String.format("[actor=%s | action=%s | entity=%s#%s] %s",
                actor, action, entityType, entityId, message);
    }
}