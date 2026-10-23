package com.club.athletics.service;

import com.club.athletics.io.ClubData;
import com.club.athletics.model.Athlete;
import com.club.athletics.model.Coach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.club.athletics.TestData.lifter;
import static com.club.athletics.TestData.session;
import static com.club.athletics.TestData.sprintCoach;
import static com.club.athletics.TestData.sprinter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RosterInitializerTest {

    private final List<String> warnings = new ArrayList<>();
    private final RosterInitializer initializer = new RosterInitializer((level, message) -> warnings.add(message));

    @Test
    void shouldApplyValidAssignmentsAndRejectInvalidOnesWithoutAborting() {
        Athlete first = sprinter("A1", 10.5, 0.15);
        Athlete second = sprinter("A2", 10.8, 0.16);
        Athlete lifter = lifter("A3", 81, 300);
        Coach coach = sprintCoach("C1", 1);
        Map<String, String> requests = new LinkedHashMap<>();
        requests.put("A1", "C1");
        requests.put("A2", "C1");   // coach full
        requests.put("A3", "C1");   // wrong discipline
        requests.put("A9", "C1");   // unknown athlete

        initializer.initialize(new ClubData(List.of(first, second, lifter), List.of(coach), requests, List.of(), 0));

        assertSame(coach, first.getCoach().orElseThrow());
        assertTrue(second.getCoach().isEmpty());
        assertTrue(lifter.getCoach().isEmpty());
        assertEquals(3, warnings.size());
    }

    @Test
    void shouldAttachSessionsAndRejectOrphans() {
        Athlete athlete = sprinter("A1", 10.5, 0.15);
        LocalDate day = LocalDate.of(2026, 10, 1);

        RosterService roster = initializer.initialize(new ClubData(List.of(athlete), List.of(), Map.of(),
                List.of(session("A1", day, 60, 5), session("GHOST", day, 60, 5)), 0));

        assertEquals(1, roster.findAthlete("A1").orElseThrow().getSessions().size());
        assertEquals(1, warnings.size());
    }
}
