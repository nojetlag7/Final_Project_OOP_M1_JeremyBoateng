package com.club.athletics.service;

import java.util.OptionalDouble;

//this week's load next to last week's
//used to see if the training jumped
public record LoadComparison(int currentLoad, int previousLoad) {

    //check to see if there was any training the week before
    //zero last week means we can't talk about a percent change
    public boolean hasBaseline() {
        return previousLoad > 0;
    }

    //calculating the percent change, nothing if last week was empty
    //positive means they trained more than the week before
    public OptionalDouble percentChange() {
        if (!hasBaseline()) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of((currentLoad - previousLoad) * 100.0 / previousLoad);
    }
}
