package com.club.athletics.util;

//somewhere to send log messages
@FunctionalInterface
public interface Logger {

    void log(LogLevel level, String message);

    default void info(String message) {
        log(LogLevel.INFO, message);
    }

    default void warn(String message) {
        log(LogLevel.WARN, message);
    }

    default void error(String message) {
        log(LogLevel.ERROR, message);
    }
}
