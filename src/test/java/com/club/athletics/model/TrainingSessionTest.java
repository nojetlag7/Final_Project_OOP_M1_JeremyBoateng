package com.club.athletics.model;

import com.club.athletics.exception.InvalidTrainingLoadException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TrainingSessionTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 10, 1, 8, 0);

    @Test
    void shouldComputeLoadAsDurationTimesIntensity() throws InvalidTrainingLoadException {
        TrainingSession session = new TrainingSession("A1", START, 60, 7);

        assertEquals(420, session.getLoad());
        assertEquals(START.plusMinutes(60), session.getEnd());
    }

    @ParameterizedTest
    @ValueSource(ints = {TrainingSession.MIN_INTENSITY, TrainingSession.MAX_INTENSITY})
    void shouldAcceptIntensityAtTheBoundaries(int intensity) {
        assertDoesNotThrow(() -> new TrainingSession("A1", START, 30, intensity));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11, -3})
    void shouldRejectIntensityOutsideRange(int intensity) {
        assertThrows(InvalidTrainingLoadException.class, () -> new TrainingSession("A1", START, 30, intensity));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void shouldRejectNonPositiveDuration(int duration) {
        assertThrows(InvalidTrainingLoadException.class, () -> new TrainingSession("A1", START, duration, 5));
    }
}
