package com.club.athletics.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.club.athletics.TestData.lifter;
import static com.club.athletics.TestData.sprinter;
import static com.club.athletics.TestData.swimmer;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** The same call on the parent type must reach each subclass's own formula. */
class AthleteScoringTest {

    @Test
    void mixedRosterDispatchesToEachSubclassFormula() {
        List<Athlete> athletes = List.of(
                sprinter("S", 10.0, 0.12),               // (10/10) * (0.15/0.12) * 100 = 125
                swimmer("W", Stroke.BACKSTROKE, 27.5),   // (25/27.5) * 1.10 * 100 = 100
                lifter("L", 64, 192));                   // 192/64 * 25 = 75

        List<Double> scores = athletes.stream().map(Athlete::calculatePerformanceScore).toList();

        assertEquals(125.0, scores.get(0), 1e-9);
        assertEquals(100.0, scores.get(1), 1e-9);
        assertEquals(75.0, scores.get(2), 1e-9);
    }

    @Test
    void disciplineComesFromTheConcreteType() {
        List<Athlete> athletes = List.of(sprinter("S", 10, 0.15), swimmer("W", Stroke.FREESTYLE, 25), lifter("L", 64, 200));

        assertEquals(List.of(Discipline.SPRINTING, Discipline.SWIMMING, Discipline.WEIGHTLIFTING),
                athletes.stream().map(Athlete::getDiscipline).toList());
    }
}
