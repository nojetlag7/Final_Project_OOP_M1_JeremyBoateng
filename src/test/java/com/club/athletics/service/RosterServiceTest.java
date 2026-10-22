package com.club.athletics.service;

import com.club.athletics.exception.AthleteManagementException;
import com.club.athletics.exception.AthleteNotFoundException;
import com.club.athletics.exception.CoachNotFoundException;
import com.club.athletics.exception.DisciplineMismatchException;
import com.club.athletics.model.Athlete;
import com.club.athletics.model.Coach;
import com.club.athletics.model.Stroke;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.club.athletics.TestData.session;
import static com.club.athletics.TestData.sprintCoach;
import static com.club.athletics.TestData.sprinter;
import static com.club.athletics.TestData.swimCoach;
import static com.club.athletics.TestData.swimmer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RosterServiceTest {

    private Athlete sprinter;
    private Athlete swimmer;
    private Coach sprintCoach;
    private Coach swimCoach;
    private RosterService roster;

    @BeforeEach
    void setUp() {
        sprinter = sprinter("A1", 10.5, 0.15);
        swimmer = swimmer("A2", Stroke.FREESTYLE, 26);
        sprintCoach = sprintCoach("C1", 2);
        swimCoach = swimCoach("C2", 2, Stroke.FREESTYLE);
        roster = new RosterService(List.of(sprinter, swimmer), List.of(sprintCoach, swimCoach));
    }

    @Test
    void shouldAssignCoachById() throws AthleteManagementException {
        Coach assigned = roster.assignCoach("A1", "C1");

        assertSame(sprintCoach, assigned);
        assertSame(sprintCoach, sprinter.getCoach().orElseThrow());
        assertEquals(List.of(swimmer), roster.getUnassignedAthletes());
    }

    @Test
    void shouldPropagateDisciplineMismatch() {
        assertThrows(DisciplineMismatchException.class, () -> roster.assignCoach("A1", "C2"));
    }

    @Test
    void shouldThrowWhenAthleteIsUnknown() {
        assertThrows(AthleteNotFoundException.class, () -> roster.assignCoach("NOPE", "C1"));
        assertTrue(roster.findAthlete("NOPE").isEmpty());
    }

    @Test
    void shouldThrowWhenCoachIsUnknown() {
        assertThrows(CoachNotFoundException.class, () -> roster.assignCoach("A1", "NOPE"));
    }

    @Test
    void shouldListOnlyCoachesThatWouldAcceptTheAthlete() {
        assertEquals(List.of(swimCoach), roster.findEligibleCoaches(swimmer));
    }

    @Test
    void shouldLogSessionAgainstTheRightAthlete() throws AthleteManagementException {
        roster.logSession(session("A2", LocalDate.of(2026, 10, 1), 60, 5));

        assertEquals(1, swimmer.getSessions().size());
        assertTrue(sprinter.getSessions().isEmpty());
    }

    @Test
    void shouldRejectSessionForUnknownAthlete() {
        assertThrows(AthleteNotFoundException.class,
                () -> roster.logSession(session("NOPE", LocalDate.of(2026, 10, 1), 60, 5)));
    }

    @Test
    void shouldRejectDuplicateIdsOnConstruction() {
        assertThrows(IllegalArgumentException.class,
                () -> new RosterService(List.of(sprinter, sprinter("A1", 11, 0.2)), List.of()));
    }
}
