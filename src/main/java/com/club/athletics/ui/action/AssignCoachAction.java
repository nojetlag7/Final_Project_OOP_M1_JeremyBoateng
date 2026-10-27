package com.club.athletics.ui.action;

import com.club.athletics.exception.AthleteManagementException;
import com.club.athletics.model.Athlete;
import com.club.athletics.model.Coach;
import com.club.athletics.model.Person;
import com.club.athletics.service.RosterService;
import com.club.athletics.ui.ConsoleIO;
import com.club.athletics.ui.MenuAction;

import java.util.List;
import java.util.stream.Collectors;

public class AssignCoachAction implements MenuAction {

    private final RosterService roster;

    public AssignCoachAction(RosterService roster) {
        this.roster = roster;
    }

    @Override
    public String label() {
        return "Assign a coach to an athlete";
    }

    @Override
    public void execute(ConsoleIO io) throws AthleteManagementException {
        io.println("Athletes without a coach: " + names(roster.getUnassignedAthletes()));
        Athlete athlete = roster.getAthleteOrThrow(io.readNonBlank("Athlete id: ").toUpperCase());

        List<Coach> eligible = roster.findEligibleCoaches(athlete);
        io.println("Coaches who can take " + athlete.getName() + ": "
                + (eligible.isEmpty() ? "none" : names(eligible)));

        Coach coach = roster.assignCoach(athlete.getId(), io.readNonBlank("Coach id: ").toUpperCase());
        io.println(athlete.getName() + " is now coached by " + coach.getName() + ".");
    }

    private static String names(List<? extends Person> people) {
        if (people.isEmpty()) {
            return "none";
        }
        return people.stream()
                .map(person -> person.getId() + " " + person.getName())
                .collect(Collectors.joining(", "));
    }
}
