package com.club.athletics.exception;

public class CoachNotFoundException extends AthleteManagementException {

    public CoachNotFoundException(String coachId) {
        super("No coach with id '" + coachId + "'");
    }
}
