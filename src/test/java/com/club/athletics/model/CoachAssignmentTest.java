package com.club.athletics.model;

import com.club.athletics.exception.CoachCapacityException;
import com.club.athletics.exception.DisciplineMismatchException;
import com.club.athletics.exception.InvalidAssignmentException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.club.athletics.TestData.liftingCoach;
import static com.club.athletics.TestData.lifter;
import static com.club.athletics.TestData.sprintCoach;
import static com.club.athletics.TestData.sprinter;
import static com.club.athletics.TestData.swimCoach;
import static com.club.athletics.TestData.swimmer;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoachAssignmentTest {

    @Test
    void shouldLinkBothSidesOnAssignment() throws InvalidAssignmentException {
        Coach coach = sprintCoach("C1", 2);
        Athlete athlete = sprinter("A1", 10.5, 0.15);

        coach.assign(athlete);

        assertSame(coach, athlete.getCoach().orElseThrow());
        assertEquals(List.of(athlete), coach.getAssignedAthletes());
    }

    @Test
    void shouldRejectAthleteFromAnotherDiscipline() {
        Coach coach = sprintCoach("C1", 2);
        Athlete athlete = swimmer("A1", Stroke.FREESTYLE, 26);

        assertThrows(DisciplineMismatchException.class, () -> coach.assign(athlete));
        assertTrue(athlete.getCoach().isEmpty());
    }

    @Test
    void shouldRejectAthleteWhenCoachIsFull() throws InvalidAssignmentException {
        Coach coach = sprintCoach("C1", 1);
        coach.assign(sprinter("A1", 10.5, 0.15));

        assertThrows(CoachCapacityException.class, () -> coach.assign(sprinter("A2", 10.8, 0.16)));
    }

    @Test
    void shouldAllowReassigningSameAthleteToFullCoach() throws InvalidAssignmentException {
        Coach coach = sprintCoach("C1", 1);
        Athlete athlete = sprinter("A1", 10.5, 0.15);
        coach.assign(athlete);

        assertDoesNotThrow(() -> coach.assign(athlete));
        assertEquals(1, coach.getAssignedAthletes().size());
    }

    @Test
    void shouldRejectSwimmerWhoseStrokeCoachIsNotCertifiedFor() {
        Coach coach = swimCoach("C1", 3, Stroke.FREESTYLE);

        InvalidAssignmentException e = assertThrows(InvalidAssignmentException.class,
                () -> coach.assign(swimmer("A1", Stroke.BUTTERFLY, 28)));
        assertTrue(e.getMessage().contains("BUTTERFLY"));
    }

    @Test
    void shouldRejectLifterAboveCertifiedWeightClass() {
        Coach coach = liftingCoach("C1", 3, 73);

        assertThrows(InvalidAssignmentException.class, () -> coach.assign(lifter("A1", 81, 300)));
        assertDoesNotThrow(() -> coach.assign(lifter("A2", 73, 260)));
    }

    @Test
    void shouldRemoveAthleteFromPreviousCoachWhenReassigned() throws InvalidAssignmentException {
        Coach first = sprintCoach("C1", 2);
        Coach second = sprintCoach("C2", 2);
        Athlete athlete = sprinter("A1", 10.5, 0.15);
        first.assign(athlete);

        second.assign(athlete);

        assertTrue(first.getAssignedAthletes().isEmpty());
        assertTrue(first.hasCapacity());
        assertSame(second, athlete.getCoach().orElseThrow());
    }

    @Test
    void shouldCheckDisciplineBeforeCapacity() throws InvalidAssignmentException {
        Coach full = sprintCoach("C1", 1);
        full.assign(sprinter("A1", 10.5, 0.15));

        assertThrows(DisciplineMismatchException.class, () -> full.assign(lifter("A2", 81, 300)));
    }
}
