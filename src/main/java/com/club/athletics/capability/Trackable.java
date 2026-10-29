package com.club.athletics.capability;

import java.time.LocalDate;
import java.util.Optional;

//something that has training sessions we can add up
//the injury check only needs the load, not the whole athlete
public interface Trackable {

    int trainingLoadBetween(LocalDate fromInclusive, LocalDate toInclusive);

    Optional<LocalDate> latestSessionDate();
}
