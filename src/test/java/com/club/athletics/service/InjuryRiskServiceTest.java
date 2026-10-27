package com.club.athletics.service;

import com.club.athletics.model.Athlete;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.club.athletics.TestData.session;
import static com.club.athletics.TestData.sprinter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InjuryRiskServiceTest {

    private static final LocalDate AS_OF = LocalDate.of(2026, 10, 14);
    private static final LocalDate PREVIOUS_WEEK = AS_OF.minusDays(10);
    private static final LocalDate CURRENT_WEEK = AS_OF.minusDays(2);

    private final InjuryRiskService service = new InjuryRiskService(40);

    /** Builds an athlete with one session per week, each with intensity 1 so load = minutes. */
    private static Athlete withLoads(String id, int previousLoad, int currentLoad) {
        Athlete athlete = sprinter(id, 10.5, 0.15);
        if (previousLoad > 0) {
            athlete.recordSession(session(id, PREVIOUS_WEEK, previousLoad, 1));
        }
        if (currentLoad > 0) {
            athlete.recordSession(session(id, CURRENT_WEEK, currentLoad, 1));
        }
        return athlete;
    }

    @Test
    void steadyLoadIsNotFlagged() {
        assertFalse(service.isAtRisk(withLoads("A", 300, 320), AS_OF));
    }

    @Test
    void increaseExactlyAtThresholdIsNotFlagged() {
        assertFalse(service.isAtRisk(withLoads("A", 100, 140), AS_OF));
    }

    @Test
    void increaseJustAboveThresholdIsFlagged() {
        assertTrue(service.isAtRisk(withLoads("A", 100, 141), AS_OF));
    }

    @Test
    void noPreviousLoadMeansNoBaselineAndIsNotFlagged() {
        Athlete newcomer = withLoads("A", 0, 500);

        assertFalse(service.isAtRisk(newcomer, AS_OF));
        assertTrue(service.compareLoads(newcomer, AS_OF).percentChange().isEmpty());
    }

    @Test
    void emptyHistoryIsNotFlagged() {
        Athlete idle = withLoads("A", 0, 0);

        assertFalse(service.isAtRisk(idle, AS_OF));
        assertEquals(new LoadComparison(0, 0), service.compareLoads(idle, AS_OF));
    }

    @Test
    void windowsAreSevenDaysEndingOnAsOfInclusive() {
        Athlete athlete = sprinter("A", 10.5, 0.15);
        athlete.recordSession(session("A", AS_OF, 10, 1));                 // current, last day
        athlete.recordSession(session("A", AS_OF.minusDays(6), 20, 1));    // current, first day
        athlete.recordSession(session("A", AS_OF.minusDays(7), 40, 1));    // previous, last day
        athlete.recordSession(session("A", AS_OF.minusDays(13), 80, 1));   // previous, first day
        athlete.recordSession(session("A", AS_OF.minusDays(14), 999, 1));  // outside both
        athlete.recordSession(session("A", AS_OF.plusDays(1), 999, 1));    // after asOf

        assertEquals(new LoadComparison(30, 120), service.compareLoads(athlete, AS_OF));
    }

    @Test
    void flagAtRiskReturnsOnlySpikingAthletes() {
        Athlete steady = withLoads("STEADY", 300, 300);
        Athlete spiking = withLoads("SPIKE", 200, 400);
        Athlete newcomer = withLoads("NEW", 0, 400);

        assertEquals(List.of(spiking), service.flagAtRiskAthletes(List.of(steady, spiking, newcomer), AS_OF));
    }

    @Test
    void latestSessionDateAcrossAthletes() {
        Athlete a = withLoads("A", 100, 0);
        Athlete b = withLoads("B", 0, 100);

        assertEquals(Optional.of(CURRENT_WEEK), service.latestSessionDate(List.of(a, b)));
        assertTrue(service.latestSessionDate(List.of()).isEmpty());
    }

    @Test
    void negativeThresholdIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new InjuryRiskService(-1));
    }
}
