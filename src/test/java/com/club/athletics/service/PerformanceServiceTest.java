package com.club.athletics.service;

import com.club.athletics.capability.PerformanceScorable;
import com.club.athletics.model.Athlete;
import com.club.athletics.model.Discipline;
import com.club.athletics.model.Stroke;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.club.athletics.TestData.lifter;
import static com.club.athletics.TestData.sprinter;
import static com.club.athletics.TestData.swimmer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PerformanceServiceTest {

    private static final double DELTA = 1e-9;
    private final PerformanceService service = new PerformanceService();

    @Test
    void sprinterAtReferenceTimesScoresOneHundred() {
        assertEquals(100.0, sprinter("A1", 10.0, 0.15).calculatePerformanceScore(), DELTA);
    }

    @Test
    void fasterSprinterScoresHigher() {
        assertTrue(sprinter("A1", 9.9, 0.14).calculatePerformanceScore()
                > sprinter("A2", 10.2, 0.14).calculatePerformanceScore());
    }

    @Test
    void swimmerScoreAppliesStrokeMultiplier() {
        assertEquals(100.0, swimmer("A1", Stroke.FREESTYLE, 25.0).calculatePerformanceScore(), DELTA);
        assertEquals(100.0, swimmer("A2", Stroke.BREASTSTROKE, 30.0).calculatePerformanceScore(), DELTA);
    }

    @Test
    void weightlifterScoreIsRelativeToWeightClass() {
        assertEquals(100.0, lifter("A1", 75, 300).calculatePerformanceScore(), DELTA);
        assertTrue(lifter("A2", 64, 280).calculatePerformanceScore()
                > lifter("A3", 102, 380).calculatePerformanceScore());
    }

    @Test
    void shouldRankBestScoreFirst() {
        Athlete slow = sprinter("SLOW", 11.0, 0.18);
        Athlete fast = sprinter("FAST", 10.0, 0.14);
        Athlete mid = sprinter("MID", 10.5, 0.16);

        List<RankedEntry<Athlete>> board = service.rank(List.of(slow, fast, mid));

        assertEquals(List.of(fast, mid, slow), board.stream().map(RankedEntry::competitor).toList());
        assertEquals(List.of(1, 2, 3), board.stream().map(RankedEntry::rank).toList());
    }

    @Test
    void equalScoresShareARankAndSkipTheNext() {
        Athlete a = lifter("A", 75, 300);   // 100
        Athlete b = lifter("B", 80, 320);   // 100
        Athlete c = lifter("C", 80, 280);   // 87.5

        List<RankedEntry<Athlete>> board = service.rank(List.of(c, a, b));

        assertEquals(List.of(1, 1, 3), board.stream().map(RankedEntry::rank).toList());
    }

    @Test
    void rankByDisciplineOnlyIncludesThatDiscipline() {
        List<Athlete> club = List.of(sprinter("S1", 10.5, 0.15), lifter("L1", 81, 300), sprinter("S2", 10.1, 0.15));

        List<RankedEntry<Athlete>> board = service.rankByDiscipline(club, Discipline.SPRINTING);

        assertEquals(List.of("S2", "S1"), board.stream().map(e -> e.competitor().getId()).toList());
    }

    @Test
    void emptyInputGivesEmptyLeaderboard() {
        assertTrue(service.rankByDiscipline(List.of(), Discipline.SWIMMING).isEmpty());
    }

    @Test
    void shouldRankAnyPerformanceScorable() {
        PerformanceScorable low = () -> 10;
        PerformanceScorable high = () -> 50;

        assertEquals(high, service.rank(List.of(low, high)).get(0).competitor());
    }
}
