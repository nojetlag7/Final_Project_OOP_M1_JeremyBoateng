package com.club.athletics.service;

import com.club.athletics.exception.SchedulingConflictException;
import com.club.athletics.model.TrainingSession;

import java.util.Collection;
import java.util.Optional;

//an athlete can't be in two sessions at the same time
//different athletes can train at the same time, that's fine
public class SchedulingService {

    //looking for a session that overlaps this one, back to back is fine
    //a new session can start exactly when the previous one ends
    public Optional<TrainingSession> findConflict(TrainingSession candidate, Collection<TrainingSession> existing) {
        return existing.stream()
                .filter(other -> other.getAthleteId().equals(candidate.getAthleteId()))
                .filter(other -> candidate.getStart().isBefore(other.getEnd())
                        && other.getStart().isBefore(candidate.getEnd()))
                .findFirst();
    }

    //throwing if the new session overlaps an existing one
    //the message includes the session it clashes with
    public void checkConflict(TrainingSession candidate, Collection<TrainingSession> existing)
            throws SchedulingConflictException {
        Optional<TrainingSession> conflict = findConflict(candidate, existing);
        if (conflict.isPresent()) {
            throw new SchedulingConflictException("Overlaps existing session " + conflict.get());
        }
    }
}
