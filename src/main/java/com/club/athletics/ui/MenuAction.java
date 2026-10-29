package com.club.athletics.ui;

import com.club.athletics.exception.AthleteManagementException;

//one option on the menu
//each option is its own class so the menu loop doesn't grow
public interface MenuAction {

    String label();

    void execute(ConsoleIO io) throws AthleteManagementException;
}
