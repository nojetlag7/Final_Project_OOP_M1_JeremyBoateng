package com.club.athletics.ui;

//thrown when the user closes the input
public class InputClosedException extends RuntimeException {

    public InputClosedException() {
        super("Console input closed");
    }
}
