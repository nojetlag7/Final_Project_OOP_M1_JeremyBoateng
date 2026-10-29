package com.club.athletics.exception;

//thrown when the coach and the athlete are in different disciplines
public class DisciplineMismatchException extends InvalidAssignmentException {

    public DisciplineMismatchException(String message) {
        super(message);
    }
}
