package com.club.athletics.exception;

/** Thrown when a coach is not allowed to take on a given athlete. */
public class InvalidAssignmentException extends AthleteManagementException {

    public InvalidAssignmentException(String message) {
        super(message);
    }
}
