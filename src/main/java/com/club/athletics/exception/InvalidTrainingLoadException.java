package com.club.athletics.exception;

//thrown when a session has a bad duration or intensity
public class InvalidTrainingLoadException extends AthleteManagementException {

    public InvalidTrainingLoadException(String message) {
        super(message);
    }
}
