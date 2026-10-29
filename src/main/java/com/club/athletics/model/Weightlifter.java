package com.club.athletics.model;

import java.time.LocalDate;

public class Weightlifter extends Athlete {

    //lifting 4 times the weight class scores 100, same scale as the others
    //so a lifter and a sprinter can both sit around 100 for a strong result
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

    //calculating the score from the total lifted compared to the weight class
    //lifting more for your size scores higher, not just lifting more
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
