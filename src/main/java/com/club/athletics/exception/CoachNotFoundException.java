package com.club.athletics.exception;

//thrown when the coach id doesn't exist
public class CoachNotFoundException extends AthleteManagementException {

    public CoachNotFoundException(String coachId) {
        super("No coach with id '" + coachId + "'");
    }
}
