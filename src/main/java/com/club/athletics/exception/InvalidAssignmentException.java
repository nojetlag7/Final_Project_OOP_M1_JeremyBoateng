package com.club.athletics.exception;

//thrown when a coach can't take this athlete
public class InvalidAssignmentException extends AthleteManagementException {

    public InvalidAssignmentException(String message) {
        super(message);
    }
}
