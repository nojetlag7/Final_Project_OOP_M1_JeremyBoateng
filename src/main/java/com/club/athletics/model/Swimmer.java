package com.club.athletics.model;

import java.time.LocalDate;
import java.util.Objects;

public class Swimmer extends Athlete {

    // A 25.00 s freestyle 50m lap scores 100.
    private static final double REFERENCE_LAP_SECONDS = 25.0;
    private static final double SCALE = 100.0;

    private final Stroke stroke;
    private final double bestLapSeconds;

    public Swimmer(String id, String name, LocalDate dateOfBirth, Stroke stroke, double bestLapSeconds) {
        super(id, name, dateOfBirth);
        if (bestLapSeconds <= 0) {
            throw new IllegalArgumentException("Lap time must be positive");
        }
        this.stroke = Objects.requireNonNull(stroke, "stroke");
        this.bestLapSeconds = bestLapSeconds;
    }

    @Override
    public Discipline getDiscipline() {
        return Discipline.SWIMMING;
    }

    @Override
    public double calculatePerformanceScore() {
        return (REFERENCE_LAP_SECONDS / bestLapSeconds) * stroke.getScoreMultiplier() * SCALE;
    }

    @Override
    protected String describeAttributes() {
        return String.format("%s, best 50m lap %.2fs", stroke, bestLapSeconds);
    }

    public Stroke getStroke() {
        return stroke;
    }

    public double getBestLapSeconds() {
        return bestLapSeconds;
    }
}
