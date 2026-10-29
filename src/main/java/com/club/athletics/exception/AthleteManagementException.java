package com.club.athletics.exception;

//base exception for when a club rule is broken
//the menu catches this one type and prints the message
public class AthleteManagementException extends Exception {

    public AthleteManagementException(String message) {
        super(message);
    }

    public AthleteManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
