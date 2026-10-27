package com.club.athletics.ui;

import com.club.athletics.exception.AthleteManagementException;

/** One menu option. New features are added as new implementations, without touching the menu. */
public interface MenuAction {

    String label();

    void execute(ConsoleIO io) throws AthleteManagementException;
}
