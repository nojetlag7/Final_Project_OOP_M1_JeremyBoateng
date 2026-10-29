package com.club.athletics.ui;

import com.club.athletics.exception.AthleteManagementException;

//one option on the menu
public interface MenuAction {

    String label();

    void execute(ConsoleIO io) throws AthleteManagementException;
}
