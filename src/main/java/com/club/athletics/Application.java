package com.club.athletics;

import com.club.athletics.io.ClasspathCsvSource;
import com.club.athletics.io.ClubData;
import com.club.athletics.io.CsvDataLoader;
import com.club.athletics.io.CsvSource;
import com.club.athletics.io.DirectoryCsvSource;
import com.club.athletics.service.InjuryRiskService;
import com.club.athletics.service.PerformanceService;
import com.club.athletics.service.RosterInitializer;
import com.club.athletics.service.RosterService;
import com.club.athletics.ui.ConsoleIO;
import com.club.athletics.ui.ConsoleMenu;
import com.club.athletics.ui.MenuAction;
import com.club.athletics.ui.action.AssignCoachAction;
import com.club.athletics.ui.action.FindAthleteAction;
import com.club.athletics.ui.action.InjuryRiskReportAction;
import com.club.athletics.ui.action.LeaderboardAction;
import com.club.athletics.ui.action.ListRosterAction;
import com.club.athletics.ui.action.LogSessionAction;
import com.club.athletics.util.ConsoleLogger;
import com.club.athletics.util.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

//starts the app
public final class Application {

    private Application() {
    }

    //loading the files then opening the menu, a folder can be passed in
    public static void main(String[] args) {
        Logger logger = new ConsoleLogger();
        CsvSource source = args.length > 0
                ? new DirectoryCsvSource(Path.of(args[0]))
                : new ClasspathCsvSource("data");

        ClubData data;
        try {
            data = new CsvDataLoader(source, logger).load();
        } catch (IOException e) {
            logger.error("Could not load club data: " + e.getMessage());
            System.exit(1);
            return;
        }

        RosterService roster = new RosterInitializer(logger).initialize(data);
        PerformanceService performance = new PerformanceService();
        InjuryRiskService injuryRisk = new InjuryRiskService(InjuryRiskService.DEFAULT_THRESHOLD_PERCENT);

        List<MenuAction> actions = List.of(
                new ListRosterAction(roster),
                new FindAthleteAction(roster),
                new AssignCoachAction(roster),
                new LogSessionAction(roster),
                new LeaderboardAction(roster, performance),
                new InjuryRiskReportAction(roster, injuryRisk));

        new ConsoleMenu(new ConsoleIO(new Scanner(System.in), System.out), actions).run();
    }
}
