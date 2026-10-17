package com.club.athletics.model;

import com.club.athletics.exception.CoachCapacityException;
import com.club.athletics.exception.DisciplineMismatchException;
import com.club.athletics.exception.InvalidAssignmentException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Coach extends Person {

    private final int maxAthletes;
    private final List<Athlete> assignedAthletes = new ArrayList<>();

    protected Coach(String id, String name, LocalDate dateOfBirth, int maxAthletes) {
        super(id, name, dateOfBirth);
        if (maxAthletes <= 0) {
            throw new IllegalArgumentException("maxAthletes must be positive");
        }
        this.maxAthletes = maxAthletes;
    }

    public abstract Discipline getDiscipline();

    /**
     * Coach-type-specific rule, run after the shared discipline and capacity checks. The
     * athlete is guaranteed to be of this coach's discipline when this is called.
     */
    protected abstract void checkSpecialization(Athlete athlete) throws InvalidAssignmentException;

    protected abstract String describeSpecialization();

    /** Template method: the order of checks is fixed here, subclasses only add their own rule. */
    public final void validateAssignment(Athlete athlete) throws InvalidAssignmentException {
        Objects.requireNonNull(athlete, "athlete");
        if (athlete.getDiscipline() != getDiscipline()) {
            throw new DisciplineMismatchException(String.format("%s coaches %s, but %s is in %s",
                    getName(), getDiscipline(), athlete.getName(), athlete.getDiscipline()));
        }
        if (!isAssigned(athlete) && !hasCapacity()) {
            throw new CoachCapacityException(String.format("%s already coaches the maximum of %d athletes",
                    getName(), maxAthletes));
        }
        checkSpecialization(athlete);
    }

    /** Validates, then moves the athlete from their previous coach (if any) to this one. */
    public void assign(Athlete athlete) throws InvalidAssignmentException {
        validateAssignment(athlete);
        if (isAssigned(athlete)) {
            return;
        }
        athlete.getCoach().ifPresent(previous -> previous.assignedAthletes.remove(athlete));
        assignedAthletes.add(athlete);
        athlete.setCoach(this);
    }

    public boolean isAssigned(Athlete athlete) {
        return assignedAthletes.contains(athlete);
    }

    public boolean hasCapacity() {
        return assignedAthletes.size() < maxAthletes;
    }

    public int getMaxAthletes() {
        return maxAthletes;
    }

    public List<Athlete> getAssignedAthletes() {
        return Collections.unmodifiableList(assignedAthletes);
    }

    @Override
    public String getSummary() {
        return String.format("%-5s %-18s %-13s athletes: %d/%d | %s",
                getId(), getName(), getDiscipline(), assignedAthletes.size(), maxAthletes,
                describeSpecialization());
    }
}
