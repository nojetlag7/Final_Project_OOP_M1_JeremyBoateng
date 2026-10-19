package com.club.athletics;

import com.club.athletics.exception.InvalidTrainingLoadException;
import com.club.athletics.model.SprintCoach;
import com.club.athletics.model.Sprinter;
import com.club.athletics.model.Stroke;
import com.club.athletics.model.SwimCoach;
import com.club.athletics.model.Swimmer;
import com.club.athletics.model.TrainingSession;
import com.club.athletics.model.WeightliftingCoach;
import com.club.athletics.model.Weightlifter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/** Short factory methods so tests read as business scenarios rather than constructor calls. */
public final class TestData {

    public static final LocalDate DOB = LocalDate.of(2000, 1, 1);

    private TestData() {
    }

    public static Sprinter sprinter(String id, double pb, double reaction) {
        return new Sprinter(id, "Sprinter " + id, DOB, pb, reaction);
    }

    public static Swimmer swimmer(String id, Stroke stroke, double lap) {
        return new Swimmer(id, "Swimmer " + id, DOB, stroke, lap);
    }

    public static Weightlifter lifter(String id, double weightClass, double total) {
        return new Weightlifter(id, "Lifter " + id, DOB, weightClass, total);
    }

    public static SprintCoach sprintCoach(String id, int max) {
        return new SprintCoach(id, "Coach " + id, DOB, max);
    }

    public static SwimCoach swimCoach(String id, int max, Stroke... strokes) {
        return new SwimCoach(id, "Coach " + id, DOB, max, Set.of(strokes));
    }

    public static WeightliftingCoach liftingCoach(String id, int max, double maxClass) {
        return new WeightliftingCoach(id, "Coach " + id, DOB, max, maxClass);
    }

    public static TrainingSession session(String athleteId, LocalDate date, int minutes, int intensity) {
        try {
            return new TrainingSession(athleteId, date.atTime(8, 0), minutes, intensity);
        } catch (InvalidTrainingLoadException e) {
            throw new IllegalArgumentException("Invalid test fixture", e);
        }
    }

    public static TrainingSession session(String athleteId, LocalDateTime start, int minutes) {
        try {
            return new TrainingSession(athleteId, start, minutes, 5);
        } catch (InvalidTrainingLoadException e) {
            throw new IllegalArgumentException("Invalid test fixture", e);
        }
    }
}
