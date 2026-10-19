package com.club.athletics.model;

import com.club.athletics.exception.InvalidAssignmentException;

import java.time.LocalDate;

public class WeightliftingCoach extends Coach {

    private final double maxCertifiedWeightClassKg;

    public WeightliftingCoach(String id, String name, LocalDate dateOfBirth, int maxAthletes,
                              double maxCertifiedWeightClassKg) {
        super(id, name, dateOfBirth, maxAthletes);
        if (maxCertifiedWeightClassKg <= 0) {
            throw new IllegalArgumentException("Certified weight class must be positive");
        }
        this.maxCertifiedWeightClassKg = maxCertifiedWeightClassKg;
    }

    @Override
    public Discipline getDiscipline() {
        return Discipline.WEIGHTLIFTING;
    }

    @Override
    protected void checkSpecialization(Athlete athlete) throws InvalidAssignmentException {
        Weightlifter lifter = (Weightlifter) athlete;
        if (lifter.getWeightClassKg() > maxCertifiedWeightClassKg) {
            throw new InvalidAssignmentException(String.format(
                    "%s is certified up to %.0fkg, but %s lifts in the %.0fkg class",
                    getName(), maxCertifiedWeightClassKg, lifter.getName(), lifter.getWeightClassKg()));
        }
    }

    @Override
    protected String describeSpecialization() {
        return String.format("classes up to %.0fkg", maxCertifiedWeightClassKg);
    }

    public double getMaxCertifiedWeightClassKg() {
        return maxCertifiedWeightClassKg;
    }
}
