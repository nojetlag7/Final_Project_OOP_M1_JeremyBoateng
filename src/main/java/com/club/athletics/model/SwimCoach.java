package com.club.athletics.model;

import com.club.athletics.exception.InvalidAssignmentException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SwimCoach extends Coach {

    private final Set<Stroke> certifiedStrokes;

    public SwimCoach(String id, String name, LocalDate dateOfBirth, int maxAthletes, Set<Stroke> certifiedStrokes) {
        super(id, name, dateOfBirth, maxAthletes);
        if (certifiedStrokes == null || certifiedStrokes.isEmpty()) {
            throw new IllegalArgumentException("A swim coach must be certified for at least one stroke");
        }
        this.certifiedStrokes = EnumSet.copyOf(certifiedStrokes);
    }

    @Override
    public Discipline getDiscipline() {
        return Discipline.SWIMMING;
    }

    //check if the coach is certified for this stroke
    @Override
    protected void checkSpecialization(Athlete athlete) throws InvalidAssignmentException {
        Swimmer swimmer = (Swimmer) athlete;
        if (!certifiedStrokes.contains(swimmer.getStroke())) {
            throw new InvalidAssignmentException(String.format("%s is not certified for %s",
                    getName(), swimmer.getStroke()));
        }
    }

    @Override
    protected String describeSpecialization() {
        return "strokes: " + certifiedStrokes.stream().map(Enum::name).collect(Collectors.joining(", "));
    }

    public Set<Stroke> getCertifiedStrokes() {
        return Collections.unmodifiableSet(certifiedStrokes);
    }
}
