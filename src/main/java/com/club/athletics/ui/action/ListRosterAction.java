package com.club.athletics.ui.action;

import com.club.athletics.model.Person;
import com.club.athletics.service.RosterService;
import com.club.athletics.ui.ConsoleIO;
import com.club.athletics.ui.MenuAction;

import java.util.List;

public class ListRosterAction implements MenuAction {

    private final RosterService roster;

    public ListRosterAction(RosterService roster) {
        this.roster = roster;
    }

    @Override
    public String label() {
        return "List coaches and athletes";
    }

    @Override
    public void execute(ConsoleIO io) {
        printSection(io, "Coaches", roster.getCoaches());
        printSection(io, "Athletes", roster.getAthletes());
    }

    // Works on Person so coaches and athletes each print their own summary format.
    private static void printSection(ConsoleIO io, String title, List<? extends Person> people) {
        io.println("");
        io.println(title + " (" + people.size() + "):");
        people.forEach(person -> io.println("  " + person.getSummary()));
    }
}
