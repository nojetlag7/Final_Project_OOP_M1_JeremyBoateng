package com.club.athletics.util;

public class ConsoleLogger implements Logger {

    // Everything goes to stdout: mixing stdout and stderr reorders lines in most consoles.
    @Override
    public void log(LogLevel level, String message) {
        System.out.println("[" + level + "] " + message);
    }
}
