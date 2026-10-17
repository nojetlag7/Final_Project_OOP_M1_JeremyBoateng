package com.club.athletics.model;

import java.time.LocalDate;

public class Weightlifter extends Athlete {

    // Lifting 4x your weight class scores 100, keeping scores on the same scale as other disciplines.
    private static final double SCALE = 25.0;

    private final double weightClassKg;
    private final double totalLiftedKg;

    public Weightlifter(String id, String name, LocalDate dateOfBirth,
                        double weightClassKg, double totalLiftedKg) {
        super(id, name, dateOfBirth);
        if (weightClassKg <= 0 || totalLiftedKg < 0) {
            throw new IllegalArgumentException("Weight class must be positive and total non-negative");
        }
        this.weightClassKg = weightClassKg;
        this.totalLiftedKg = totalLiftedKg;
    }

    @Override
    public Discipline getDiscipline() {
        return Discipline.WEIGHTLIFTING;
    }

    @Override
    public double calculatePerformanceScore() {
        return totalLiftedKg / weightClassKg * SCALE;
    }

    @Override
    protected String describeAttributes() {
        return String.format("%.0fkg class, total %.0fkg", weightClassKg, totalLiftedKg);
    }

    public double getWeightClassKg() {
        return weightClassKg;
    }

    public double getTotalLiftedKg() {
        return totalLiftedKg;
    }
}
