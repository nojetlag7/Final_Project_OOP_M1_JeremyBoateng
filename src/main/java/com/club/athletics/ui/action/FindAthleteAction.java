package com.club.athletics.ui.action;

import com.club.athletics.exception.AthleteNotFoundException;
import com.club.athletics.model.Athlete;
import com.club.athletics.service.RosterService;
import com.club.athletics.ui.ConsoleIO;
import com.club.athletics.ui.MenuAction;

public class FindAthleteAction implements MenuAction {

    private final RosterService roster;

    public FindAthleteAction(RosterService roster) {
        this.roster = roster;
    }

    @Override
    public String label() {
        return "Show athlete details";
    }

    @Override
    public void execute(ConsoleIO io) throws AthleteNotFoundException {
        Athlete athlete = roster.getAthleteOrThrow(io.readNonBlank("Athlete id: ").toUpperCase());
        io.println(athlete.getSummary());
        io.printf("  Performance score: %.1f%n", athlete.calculatePerformanceScore());
        io.printf("  Sessions logged:   %d%n", athlete.getSessions().size());
        athlete.getSessions().forEach(session -> io.println("    " + session));
    }
}
