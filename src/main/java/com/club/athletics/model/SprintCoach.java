package com.club.athletics.model;

import java.time.LocalDate;

public class SprintCoach extends Coach {

    public SprintCoach(String id, String name, LocalDate dateOfBirth, int maxAthletes) {
        super(id, name, dateOfBirth, maxAthletes);
    }

    @Override
    public Discipline getDiscipline() {
        return Discipline.SPRINTING;
    }

    /** Sprint coaching is not certified per event, so any sprinter is acceptable. */
    @Override
    protected void checkSpecialization(Athlete athlete) {
    }

    @Override
    protected String describeSpecialization() {
        return "all sprinters";
    }
}
