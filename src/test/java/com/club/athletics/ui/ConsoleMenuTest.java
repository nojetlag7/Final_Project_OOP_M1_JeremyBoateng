package com.club.athletics.ui;

import com.club.athletics.exception.AthleteManagementException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleMenuTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private void run(String input, List<MenuAction> actions) {
        ConsoleIO io = new ConsoleIO(new Scanner(input),
                new PrintStream(output, true, StandardCharsets.UTF_8));
        new ConsoleMenu(io, actions).run();
    }

    private String output() {
        return output.toString(StandardCharsets.UTF_8);
    }

    private static MenuAction action(String label, Runnable body) {
        return new MenuAction() {
            @Override
            public String label() {
                return label;
            }

            @Override
            public void execute(ConsoleIO io) {
                body.run();
            }
        };
    }

    @Test
    void shouldRunChosenActionsUntilExit() {
        AtomicInteger calls = new AtomicInteger();

        run("1\n1\n0\n", List.of(action("Count", calls::incrementAndGet)));

        assertEquals(2, calls.get());
        assertTrue(output().contains("Goodbye."));
    }

    @Test
    void shouldReportDomainErrorAndKeepRunning() {
        AtomicInteger calls = new AtomicInteger();
        MenuAction failing = new MenuAction() {
            @Override
            public String label() {
                return "Fail";
            }

            @Override
            public void execute(ConsoleIO io) throws AthleteManagementException {
                throw new AthleteManagementException("coach is full");
            }
        };

        run("1\n2\n0\n", List.of(failing, action("Count", calls::incrementAndGet)));

        assertTrue(output().contains("Error: coach is full"));
        assertEquals(1, calls.get());
    }

    @Test
    void shouldRepromptOnNonNumericAndUnknownOptions() {
        run("abc\n99\n0\n", List.of());

        assertTrue(output().contains("'abc' is not a whole number"));
        assertTrue(output().contains("Unknown option 99"));
    }

    @Test
    void shouldExitCleanlyWhenInputEnds() {
        run("", List.of());

        assertTrue(output().contains("Goodbye."));
    }
}
