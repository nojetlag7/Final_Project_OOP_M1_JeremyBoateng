package com.club.athletics.service;

import com.club.athletics.exception.SchedulingConflictException;
import com.club.athletics.model.TrainingSession;

import java.util.Collection;
import java.util.Optional;

/** An athlete cannot be in two sessions at once. */
public class SchedulingService {

    /**
     * Sessions are half-open {@code [start, end)} windows, so a session may start exactly when the
     * previous one ends.
     */
    public Optional<TrainingSession> findConflict(TrainingSession candidate, Collection<TrainingSession> existing) {
        return existing.stream()
                .filter(other -> other.getAthleteId().equals(candidate.getAthleteId()))
                .filter(other -> candidate.getStart().isBefore(other.getEnd())
                        && other.getStart().isBefore(candidate.getEnd()))
                .findFirst();
    }

    public void checkConflict(TrainingSession candidate, Collection<TrainingSession> existing)
            throws SchedulingConflictException {
        Optional<TrainingSession> conflict = findConflict(candidate, existing);
        if (conflict.isPresent()) {
            throw new SchedulingConflictException("Overlaps existing session " + conflict.get());
        }
    }
}
