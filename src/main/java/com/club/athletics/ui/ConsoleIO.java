package com.club.athletics.ui;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/** Typed console input. Format mistakes are re-prompted here; business rules are not checked here. */
public class ConsoleIO {

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Scanner scanner;
    private final PrintStream out;

    public ConsoleIO(Scanner scanner, PrintStream out) {
        this.scanner = scanner;
        this.out = out;
    }

    public void println(String text) {
        out.println(text);
    }

    public void printf(String format, Object... args) {
        out.printf(format, args);
    }

    public String readLine(String prompt) {
        out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new InputClosedException();
        }
        return scanner.nextLine().trim();
    }

    public String readNonBlank(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) {
                return value;
            }
            out.println("A value is required.");
        }
    }

    public int readInt(String prompt) {
        while (true) {
            String value = readLine(prompt);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                out.println("'" + value + "' is not a whole number, please try again.");
            }
        }
    }

    public LocalDateTime readDateTime(String prompt) {
        while (true) {
            String value = readLine(prompt);
            try {
                return LocalDateTime.parse(value, DATE_TIME_FORMAT);
            } catch (DateTimeParseException e) {
                out.println("Expected a date and time like 2026-10-14 07:30.");
            }
        }
    }

    /** An empty answer keeps {@code defaultValue}. */
    public LocalDate readDate(String prompt, LocalDate defaultValue) {
        while (true) {
            String value = readLine(prompt);
            if (value.isEmpty()) {
                return defaultValue;
            }
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                out.println("Expected a date like 2026-10-14.");
            }
        }
    }
}
