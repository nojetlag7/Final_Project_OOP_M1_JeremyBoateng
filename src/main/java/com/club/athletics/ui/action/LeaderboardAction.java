package com.club.athletics.ui.action;

import com.club.athletics.model.Athlete;
import com.club.athletics.model.Discipline;
import com.club.athletics.service.PerformanceService;
import com.club.athletics.service.RankedEntry;
import com.club.athletics.service.RosterService;
import com.club.athletics.ui.ConsoleIO;
import com.club.athletics.ui.MenuAction;

import java.util.List;

public class LeaderboardAction implements MenuAction {

    private final RosterService roster;
    private final PerformanceService performance;

    public LeaderboardAction(RosterService roster, PerformanceService performance) {
        this.roster = roster;
        this.performance = performance;
    }

    @Override
    public String label() {
        return "Show leaderboards";
    }

    @Override
    //printing a leaderboard for each discipline
    public void execute(ConsoleIO io) {
        for (Discipline discipline : Discipline.values()) {
            List<RankedEntry<Athlete>> board = performance.rankByDiscipline(roster.getAthletes(), discipline);
            io.println("");
            io.println(discipline + ":");
            if (board.isEmpty()) {
                io.println("  no athletes");
            }
            board.forEach(entry -> io.printf("  %2d. %-18s %6.1f%n",
                    entry.rank(), entry.competitor().getName(), entry.score()));
        }
    }
}
