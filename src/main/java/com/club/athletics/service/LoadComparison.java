package com.club.athletics.service;

import java.util.OptionalDouble;

//this week's load next to last week's
public record LoadComparison(int currentLoad, int previousLoad) {

    //check to see if there was any training the week before
    public boolean hasBaseline() {
        return previousLoad > 0;
    }

    //calculating the percent change, nothing if last week was empty
    public OptionalDouble percentChange() {
        if (!hasBaseline()) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of((currentLoad - previousLoad) * 100.0 / previousLoad);
    }
}
