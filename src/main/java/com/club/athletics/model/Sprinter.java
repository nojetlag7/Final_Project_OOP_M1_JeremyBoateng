package com.club.athletics.model;

import java.time.LocalDate;

public class Sprinter extends Athlete {

    // A 10.00 s 100m with a 0.150 s reaction scores 100; faster athletes score higher.
    private static final double REFERENCE_TIME_SECONDS = 10.0;
    private static final double REFERENCE_REACTION_SECONDS = 0.15;
    private static final double SCALE = 100.0;

    private final double personalBestSeconds;
    private final double reactionTimeSeconds;

    public Sprinter(String id, String name, LocalDate dateOfBirth,
                    double personalBestSeconds, double reactionTimeSeconds) {
        super(id, name, dateOfBirth);
        if (personalBestSeconds <= 0 || reactionTimeSeconds <= 0) {
            throw new IllegalArgumentException("Sprint times must be positive");
        }
        this.personalBestSeconds = personalBestSeconds;
        this.reactionTimeSeconds = reactionTimeSeconds;
    }

    @Override
    public Discipline getDiscipline() {
        return Discipline.SPRINTING;
    }

    @Override
    public double calculatePerformanceScore() {
        return (REFERENCE_TIME_SECONDS / personalBestSeconds)
                * (REFERENCE_REACTION_SECONDS / reactionTimeSeconds)
                * SCALE;
    }

    @Override
    protected String describeAttributes() {
        return String.format("100m PB %.2fs, reaction %.3fs", personalBestSeconds, reactionTimeSeconds);
    }

    public double getPersonalBestSeconds() {
        return personalBestSeconds;
    }

    public double getReactionTimeSeconds() {
        return reactionTimeSeconds;
    }
}
