package com.club.athletics.service;

import com.club.athletics.capability.Trackable;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

//looking at week to week load to see who trained too hard
//a high load on its own is fine, it's the sudden jump that gets flagged
public class InjuryRiskService {

    //anything over 40% more than last week counts as overloaded
    //exactly 40% is not flagged, it has to be more than that
    public static final int DEFAULT_THRESHOLD_PERCENT = 40;
    private static final int WINDOW_DAYS = 7;

    private final int thresholdPercent;

    public InjuryRiskService(int thresholdPercent) {
        if (thresholdPercent < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
        }
        this.thresholdPercent = thresholdPercent;
    }

    public int getThresholdPercent() {
        return thresholdPercent;
    }

    //comparing the last 7 days with the 7 days before that
    //the date passed in is the last day of the current week
    public LoadComparison compareLoads(Trackable trackable, LocalDate asOf) {
        LocalDate currentStart = asOf.minusDays(WINDOW_DAYS - 1);
        LocalDate previousEnd = currentStart.minusDays(1);
        LocalDate previousStart = previousEnd.minusDays(WINDOW_DAYS - 1);
        return new LoadComparison(
                trackable.trainingLoadBetween(currentStart, asOf),
                trackable.trainingLoadBetween(previousStart, previousEnd));
    }

    //flagging when an athlete has overloaded
    //if last week had no sessions there's nothing to compare, so they're not flagged
    public boolean isAtRisk(Trackable trackable, LocalDate asOf) {
        LoadComparison loads = compareLoads(trackable, asOf);
        return loads.hasBaseline()
                && (long) loads.currentLoad() * 100 > (long) loads.previousLoad() * (100 + thresholdPercent);
    }

    //getting the athletes that are overloaded
    //returns the athletes, not just text, so the result can be checked
    public <T extends Trackable> List<T> flagAtRiskAthletes(Collection<T> trackables, LocalDate asOf) {
        return trackables.stream()
                .filter(trackable -> isAtRisk(trackable, asOf))
                .toList();
    }

    //finding the most recent session date in the data
    //the menu uses this when you just press enter on the report
    public Optional<LocalDate> latestSessionDate(Collection<? extends Trackable> trackables) {
        return trackables.stream()
                .map(Trackable::latestSessionDate)
                .flatMap(Optional::stream)
                .max(Comparator.naturalOrder());
    }
}
