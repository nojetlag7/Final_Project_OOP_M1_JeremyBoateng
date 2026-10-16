package com.club.athletics.exception;

public class AthleteNotFoundException extends AthleteManagementException {

    public AthleteNotFoundException(String athleteId) {
        super("No athlete with id '" + athleteId + "'");
    }
}
