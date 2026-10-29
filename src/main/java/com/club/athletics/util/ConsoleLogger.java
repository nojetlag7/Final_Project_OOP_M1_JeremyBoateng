package com.club.athletics.util;

public class ConsoleLogger implements Logger {

    //printing on one stream so the lines don't get mixed up
    @Override
    public void log(LogLevel level, String message) {
        System.out.println("[" + level + "] " + message);
    }
}
