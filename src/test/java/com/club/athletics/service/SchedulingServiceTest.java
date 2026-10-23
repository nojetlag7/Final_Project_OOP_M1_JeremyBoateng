package com.club.athletics.service;

import com.club.athletics.exception.AthleteManagementException;
import com.club.athletics.exception.SchedulingConflictException;
import com.club.athletics.model.Athlete;
import com.club.athletics.model.TrainingSession;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.club.athletics.TestData.session;
import static com.club.athletics.TestData.sprinter;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchedulingServiceTest {

    private static final LocalDateTime NINE = LocalDateTime.of(2026, 10, 14, 9, 0);

    private final SchedulingService scheduling = new SchedulingService();
    private final TrainingSession nineToTen = session("A1", NINE, 60);

    @Test
    void overlappingSessionForSameAthleteIsAConflict() {
        TrainingSession halfPastNine = session("A1", NINE.plusMinutes(30), 60);

        assertThrows(SchedulingConflictException.class,
                () -> scheduling.checkConflict(halfPastNine, List.of(nineToTen)));
    }

    @Test
    void sessionInsideAnotherIsAConflict() {
        assertTrue(scheduling.findConflict(session("A1", NINE.plusMinutes(10), 20), List.of(nineToTen)).isPresent());
    }

    @Test
    void adjacentSessionsDoNotConflict() {
        TrainingSession tenToEleven = session("A1", NINE.plusMinutes(60), 60);

        assertDoesNotThrow(() -> scheduling.checkConflict(tenToEleven, List.of(nineToTen)));
    }

    @Test
    void differentAthletesMayTrainAtTheSameTime() {
        assertDoesNotThrow(() -> scheduling.checkConflict(session("A2", NINE, 60), List.of(nineToTen)));
    }

    @Test
    void rosterRejectsOverlappingSessionAndKeepsHistoryUnchanged() throws AthleteManagementException {
        Athlete athlete = sprinter("A1", 10.5, 0.15);
        RosterService roster = new RosterService(List.of(athlete), List.of());
        roster.logSession(nineToTen);

        assertThrows(SchedulingConflictException.class,
                () -> roster.logSession(session("A1", NINE.plusMinutes(45), 30)));
        assertEquals(1, athlete.getSessions().size());
    }
}
