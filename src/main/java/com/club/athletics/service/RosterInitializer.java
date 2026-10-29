package com.club.athletics.service;

import com.club.athletics.exception.AthleteManagementException;
import com.club.athletics.io.ClubData;
import com.club.athletics.model.TrainingSession;
import com.club.athletics.util.Logger;

import java.util.Map;

//putting the file data onto the roster, bad assignments get skipped
//the csv goes through the same checks as typing it in the menu
public class RosterInitializer {

    private final Logger logger;

    public RosterInitializer(Logger logger) {
        this.logger = logger;
    }

    //applying the coaches and sessions from the files
    //if one fails it gets logged and the rest still load
    public RosterService initialize(ClubData data) {
        RosterService roster = new RosterService(data.athletes(), data.coaches());

        for (Map.Entry<String, String> request : data.requestedAssignments().entrySet()) {
            try {
                roster.assignCoach(request.getKey(), request.getValue());
            } catch (AthleteManagementException e) {
                logger.warn("Initial assignment " + request.getKey() + " -> " + request.getValue()
                        + " rejected: " + e.getMessage());
            }
        }

        for (TrainingSession session : data.sessions()) {
            try {
                roster.logSession(session);
            } catch (AthleteManagementException e) {
                logger.warn("Session " + session + " rejected: " + e.getMessage());
            }
        }
        return roster;
    }
}
