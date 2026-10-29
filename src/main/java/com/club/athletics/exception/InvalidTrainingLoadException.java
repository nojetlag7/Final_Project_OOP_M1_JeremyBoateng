package com.club.athletics.exception;

//thrown when a session has a bad duration or intensity
//duration has to be more than 0, intensity has to be 1 to 10
public class InvalidTrainingLoadException extends AthleteManagementException {

    public InvalidTrainingLoadException(String message) {
        super(message);
    }
}
