package com.club.athletics.service;

import com.club.athletics.capability.Trackable;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Flags athletes whose weekly training load jumped sharply. Sudden load spikes, rather than
 * high load as such, are what sport-science research links most strongly to injuries.
 */
public class InjuryRiskService {

    // Week-over-week increases above roughly 30-50% are commonly treated as the danger zone;
    // 40% sits in the middle and can be tuned per club through the constructor.
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

    public LoadComparison compareLoads(Trackable trackable, LocalDate asOf) {
        LocalDate currentStart = asOf.minusDays(WINDOW_DAYS - 1);
        LocalDate previousEnd = currentStart.minusDays(1);
        LocalDate previousStart = previousEnd.minusDays(WINDOW_DAYS - 1);
        return new LoadComparison(
                trackable.trainingLoadBetween(currentStart, asOf),
                trackable.trainingLoadBetween(previousStart, previousEnd));
    }

    /** Strictly above the threshold; compared in integers so an exact 40% is never misjudged by rounding. */
    public boolean isAtRisk(Trackable trackable, LocalDate asOf) {
        LoadComparison loads = compareLoads(trackable, asOf);
        return loads.hasBaseline()
                && (long) loads.currentLoad() * 100 > (long) loads.previousLoad() * (100 + thresholdPercent);
    }

    public <T extends Trackable> List<T> flagAtRiskAthletes(Collection<T> trackables, LocalDate asOf) {
        return trackables.stream()
                .filter(trackable -> isAtRisk(trackable, asOf))
                .toList();
    }

    public Optional<LocalDate> latestSessionDate(Collection<? extends Trackable> trackables) {
        return trackables.stream()
                .map(Trackable::latestSessionDate)
                .flatMap(Optional::stream)
                .max(Comparator.naturalOrder());
    }
}
