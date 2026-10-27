package com.club.athletics.ui;

/**
 * Console input reached end-of-stream (Ctrl+D / Ctrl+Z, or piped input ran out). Unchecked
 * because it can happen on any read and the only sensible reaction is to leave the menu.
 */
public class InputClosedException extends RuntimeException {

    public InputClosedException() {
        super("Console input closed");
    }
}
