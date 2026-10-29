package com.club.athletics.exception;

//thrown when the coach is already full
public class CoachCapacityException extends InvalidAssignmentException {

    public CoachCapacityException(String message) {
        super(message);
    }
}
