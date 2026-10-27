package com.club.athletics.ui.action;

import com.club.athletics.model.Athlete;
import com.club.athletics.service.InjuryRiskService;
import com.club.athletics.service.LoadComparison;
import com.club.athletics.service.RosterService;
import com.club.athletics.ui.ConsoleIO;
import com.club.athletics.ui.MenuAction;

import java.time.LocalDate;
import java.util.List;

public class InjuryRiskReportAction implements MenuAction {

    private final RosterService roster;
    private final InjuryRiskService injuryRisk;

    public InjuryRiskReportAction(RosterService roster, InjuryRiskService injuryRisk) {
        this.roster = roster;
        this.injuryRisk = injuryRisk;
    }

    @Override
    public String label() {
        return "Injury-risk report";
    }

    @Override
    public void execute(ConsoleIO io) {
        List<Athlete> athletes = roster.getAthletes();
        // Defaulting to the latest logged session keeps the report meaningful for historical data.
        LocalDate defaultDate = injuryRisk.latestSessionDate(athletes).orElse(LocalDate.now());
        LocalDate asOf = io.readDate("Assess as of [" + defaultDate + "]: ", defaultDate);

        io.println("");
        io.printf("Week ending %s vs. week before (flag above +%d%%)%n", asOf, injuryRisk.getThresholdPercent());
        io.printf("  %-18s %8s %8s %10s%n", "Athlete", "Previous", "Current", "Change");
        for (Athlete athlete : athletes) {
            LoadComparison loads = injuryRisk.compareLoads(athlete, asOf);
            String change = loads.percentChange().isPresent()
                    ? String.format("%+.0f%%", loads.percentChange().getAsDouble())
                    : "no base";
            String flag = injuryRisk.isAtRisk(athlete, asOf) ? "  << AT RISK" : "";
            io.printf("  %-18s %8d %8d %10s%s%n",
                    athlete.getName(), loads.previousLoad(), loads.currentLoad(), change, flag);
        }

        List<Athlete> flagged = injuryRisk.flagAtRiskAthletes(athletes, asOf);
        io.println("");
        io.println(flagged.isEmpty()
                ? "No athletes at risk."
                : flagged.size() + " athlete(s) at risk: "
                        + String.join(", ", flagged.stream().map(Athlete::getName).toList()));
    }
}
