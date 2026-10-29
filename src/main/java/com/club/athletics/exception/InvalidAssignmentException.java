package com.club.athletics.exception;

//thrown when a coach can't take this athlete
//wrong discipline, coach is full, or they're not certified for them
public class InvalidAssignmentException extends AthleteManagementException {

    public InvalidAssignmentException(String message) {
        super(message);
    }
}
