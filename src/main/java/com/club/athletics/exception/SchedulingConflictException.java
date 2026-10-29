package com.club.athletics.exception;

//thrown when a session overlaps another one
public class SchedulingConflictException extends AthleteManagementException {

    public SchedulingConflictException(String message) {
        super(message);
    }
}
