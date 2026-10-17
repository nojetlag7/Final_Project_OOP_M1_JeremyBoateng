package com.club.athletics.model;

public enum Stroke {
    // Multipliers compensate for strokes being naturally slower than freestyle, so a strong
    // breaststroke swimmer is not ranked below an average freestyler just because of the stroke.
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
