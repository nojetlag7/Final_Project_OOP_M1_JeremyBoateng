package com.club.athletics.service;

import java.util.OptionalDouble;

/** Training load of the most recent week compared with the week before it. */
public record LoadComparison(int currentLoad, int previousLoad) {

    public boolean hasBaseline() {
        return previousLoad > 0;
    }

    /** Empty when there is no previous load, because a change from zero has no meaningful percentage. */
    public OptionalDouble percentChange() {
        if (!hasBaseline()) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of((currentLoad - previousLoad) * 100.0 / previousLoad);
    }
}
