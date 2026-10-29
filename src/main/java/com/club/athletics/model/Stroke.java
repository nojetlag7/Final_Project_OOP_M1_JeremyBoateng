package com.club.athletics.model;

public enum Stroke {
    //slower strokes get a bigger number so they aren't ranked lower just for the stroke
    //breaststroke is the slowest so it gets the biggest one
    FREESTYLE(1.00),
    BACKSTROKE(1.10),
    BUTTERFLY(1.05),
    BREASTSTROKE(1.20);

    private final double scoreMultiplier;

    Stroke(double scoreMultiplier) {
        this.scoreMultiplier = scoreMultiplier;
    }

    public double getScoreMultiplier() {
        return scoreMultiplier;
    }
}
