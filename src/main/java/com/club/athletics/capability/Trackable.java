package com.club.athletics.capability;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Anything with a training history whose load can be analysed over time. Exposes only the
 * aggregates the analysis needs, so this package does not depend on the model package.
 */
public interface Trackable {

    int trainingLoadBetween(LocalDate fromInclusive, LocalDate toInclusive);

    Optional<LocalDate> latestSessionDate();
}
