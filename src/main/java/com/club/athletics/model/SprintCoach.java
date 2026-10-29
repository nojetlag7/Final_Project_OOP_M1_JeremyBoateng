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

    //sprint coaches can take any sprinter
    @Override
    protected void checkSpecialization(Athlete athlete) {
    }

    @Override
    protected String describeSpecialization() {
        return "all sprinters";
    }
}
