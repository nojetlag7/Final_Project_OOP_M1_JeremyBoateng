package com.club.athletics.model;

import com.club.athletics.exception.InvalidTrainingLoadException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

//one training session that already happened, so it can't be edited after
public final class TrainingSession {

    public static final int MIN_INTENSITY = 1;
    public static final int MAX_INTENSITY = 10;

    private final String athleteId;
    private final LocalDateTime start;
    private final int durationMinutes;
    private final int intensity;

    public TrainingSession(String athleteId, LocalDateTime start, int durationMinutes, int intensity)
            throws InvalidTrainingLoadException {
        this.athleteId = Objects.requireNonNull(athleteId, "athleteId");
        this.start = Objects.requireNonNull(start, "start");
        if (durationMinutes <= 0) {
            throw new InvalidTrainingLoadException("Duration must be positive, got " + durationMinutes);
        }
        if (intensity < MIN_INTENSITY || intensity > MAX_INTENSITY) {
            throw new InvalidTrainingLoadException(String.format(
                    "Intensity must be between %d and %d, got %d", MIN_INTENSITY, MAX_INTENSITY, intensity));
        }
        this.durationMinutes = durationMinutes;
        this.intensity = intensity;
    }

    //calculating the load of a single training session
    //minutes times intensity, a harder or longer session counts for more
    public int getLoad() {
        return durationMinutes * intensity;
    }

    public String getAthleteId() {
        return athleteId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return start.plusMinutes(durationMinutes);
    }

    public LocalDate getDate() {
        return start.toLocalDate();
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public int getIntensity() {
        return intensity;
    }

    @Override
    public String toString() {
        return String.format("%s %s %d min @ %d (load %d)", athleteId, start, durationMinutes, intensity, getLoad());
    }
}
