package com.club.athletics.exception;

//thrown when the athlete id doesn't exist
public class AthleteNotFoundException extends AthleteManagementException {

    public AthleteNotFoundException(String athleteId) {
        super("No athlete with id '" + athleteId + "'");
    }
}
