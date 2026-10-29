package com.club.athletics.ui.action;

import com.club.athletics.exception.AthleteManagementException;
import com.club.athletics.model.Athlete;
import com.club.athletics.model.TrainingSession;
import com.club.athletics.service.RosterService;
import com.club.athletics.ui.ConsoleIO;
import com.club.athletics.ui.MenuAction;

import java.time.LocalDateTime;

public class LogSessionAction implements MenuAction {

    private final RosterService roster;

    public LogSessionAction(RosterService roster) {
        this.roster = roster;
    }

    @Override
    public String label() {
        return "Log a training session";
    }

    @Override
    public void execute(ConsoleIO io) throws AthleteManagementException {
        //checking the athlete exists before asking for the rest
        Athlete athlete = roster.getAthleteOrThrow(io.readNonBlank("Athlete id: ").toUpperCase());
        LocalDateTime start = io.readDateTime("Start (yyyy-MM-dd HH:mm): ");
        int duration = io.readInt("Duration in minutes: ");
        int intensity = io.readInt("Intensity (" + TrainingSession.MIN_INTENSITY + "-"
                + TrainingSession.MAX_INTENSITY + "): ");

        TrainingSession session = new TrainingSession(athlete.getId(), start, duration, intensity);
        roster.logSession(session);
        io.println("Logged: " + session);
    }
}
