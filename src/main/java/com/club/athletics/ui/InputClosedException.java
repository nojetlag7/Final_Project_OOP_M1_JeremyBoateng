package com.club.athletics.ui;

//thrown when the user closes the input, like ctrl+z
//the menu treats this as exit instead of an error
public class InputClosedException extends RuntimeException {

    public InputClosedException() {
        super("Console input closed");
    }
}
