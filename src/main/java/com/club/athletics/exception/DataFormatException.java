package com.club.athletics.exception;

//thrown when a csv row can't be read
public class DataFormatException extends AthleteManagementException {

    public DataFormatException(String message) {
        super(message);
    }

    public DataFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
