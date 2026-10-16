package com.club.athletics.exception;

/** Thrown when a row of a data file cannot be turned into a domain object. */
public class DataFormatException extends AthleteManagementException {

    public DataFormatException(String message) {
        super(message);
    }

    public DataFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
