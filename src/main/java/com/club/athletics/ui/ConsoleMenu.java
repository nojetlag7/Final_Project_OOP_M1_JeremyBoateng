package com.club.athletics.ui;

import com.club.athletics.exception.AthleteManagementException;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Runs until the user picks Exit or input ends. A failed action never ends the session. */
public class ConsoleMenu {

    private static final int EXIT_OPTION = 0;

    private final ConsoleIO io;
    private final Map<Integer, MenuAction> actions = new TreeMap<>();

    public ConsoleMenu(ConsoleIO io, List<MenuAction> actions) {
        this.io = io;
        for (int i = 0; i < actions.size(); i++) {
            this.actions.put(i + 1, actions.get(i));
        }
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            try {
                int choice = io.readInt("Choose an option: ");
                if (choice == EXIT_OPTION) {
                    running = false;
                } else {
                    runAction(choice);
                }
            } catch (InputClosedException e) {
                running = false;
            }
        }
        io.println("Goodbye.");
    }

    private void runAction(int choice) {
        MenuAction action = actions.get(choice);
        if (action == null) {
            io.println("Unknown option " + choice + ".");
            return;
        }
        try {
            action.execute(io);
        } catch (AthleteManagementException e) {
            io.println("Error: " + e.getMessage());
        }
    }

    private void printMenu() {
        io.println("");
        io.println("=== Athlete Management ===");
        actions.forEach((number, action) -> io.printf("%d. %s%n", number, action.label()));
        io.printf("%d. Exit%n", EXIT_OPTION);
    }
}
