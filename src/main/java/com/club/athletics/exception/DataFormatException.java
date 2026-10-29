package com.club.athletics.exception;

//thrown when a csv row can't be read
//wrong column count, a bad number, a bad date, or a duplicate id
public class DataFormatException extends AthleteManagementException {

    public DataFormatException(String message) {
        super(message);
    }

    public DataFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
