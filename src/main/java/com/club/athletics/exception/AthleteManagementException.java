package com.club.athletics.exception;

/**
 * Base type for every business rule violation in the application, so the console loop can
 * report any domain error in one place without also catching programming errors.
 */
public class AthleteManagementException extends Exception {

    public AthleteManagementException(String message) {
        super(message);
    }

    public AthleteManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
