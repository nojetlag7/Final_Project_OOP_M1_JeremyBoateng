package com.club.athletics.io;

import com.club.athletics.model.Swimmer;
import com.club.athletics.model.Stroke;
import com.club.athletics.model.Weightlifter;
import com.club.athletics.util.LogLevel;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvDataLoaderTest {

    private static final String COACH_HEADER = "id,name,dateOfBirth,discipline,maxAthletes,specialization\n";
    private static final String ATHLETE_HEADER = "id,name,dateOfBirth,discipline,attr1,attr2,coachId\n";
    private static final String SESSION_HEADER = "athleteId,dateTime,durationMinutes,intensity\n";

    private final Map<String, String> files = new HashMap<>();
    private final List<String> warnings = new ArrayList<>();

    private ClubData load(String coaches, String athletes, String sessions) throws IOException {
        files.put(CsvDataLoader.COACHES_FILE, COACH_HEADER + coaches);
        files.put(CsvDataLoader.ATHLETES_FILE, ATHLETE_HEADER + athletes);
        files.put(CsvDataLoader.SESSIONS_FILE, SESSION_HEADER + sessions);
        CsvSource source = name -> {
            String content = files.get(name);
            if (content == null) {
                throw new FileNotFoundException(name);
            }
            return new StringReader(content);
        };
        return new CsvDataLoader(source, (level, message) -> {
            if (level == LogLevel.WARN) {
                warnings.add(message);
            }
        }).load();
    }

    @Test
    void shouldBuildCorrectSubclassForEachDiscipline() throws IOException {
        ClubData data = load(
                "C1,Coach,1980-01-01,SWIMMING,2,FREESTYLE|BUTTERFLY\n",
                "A1,Swim,2000-01-01,SWIMMING,BUTTERFLY,28.0,C1\n"
                        + "A2,Lift,2000-01-01,WEIGHTLIFTING,81,300,\n",
                "A1,2026-10-01T08:00,60,5\n");

        assertEquals(2, data.athletes().size());
        Swimmer swimmer = assertInstanceOf(Swimmer.class, data.athletes().get(0));
        assertEquals(Stroke.BUTTERFLY, swimmer.getStroke());
        assertInstanceOf(Weightlifter.class, data.athletes().get(1));
        assertEquals(Map.of("A1", "C1"), data.requestedAssignments());
        assertEquals(1, data.sessions().size());
        assertEquals(0, data.skippedRows());
    }

    @Test
    void shouldSkipMalformedRowsAndKeepLoadingTheRest() throws IOException {
        ClubData data = load(
                "",
                "A1,Fast,2000-01-01,SPRINTING,abc,0.15,\n"     // non-numeric
                        + "A2,Short,2000-01-01,SPRINTING,10.5\n"    // missing columns
                        + "A3,Odd,2000-01-01,CURLING,1,1,\n"        // unknown discipline
                        + "A4,Neg,2000-01-01,SPRINTING,-10,0.15,\n" // rejected by constructor
                        + "A5,Good,2000-01-01,SPRINTING,10.5,0.15,\n",
                "");

        assertEquals(1, data.athletes().size());
        assertEquals("A5", data.athletes().get(0).getId());
        assertEquals(4, data.skippedRows());
        assertEquals(4, warnings.size());
        assertTrue(warnings.get(0).contains("line 2"));
    }

    @Test
    void shouldSkipDuplicateIdsAcrossAthletesAndCoaches() throws IOException {
        ClubData data = load(
                "X1,Coach,1980-01-01,SPRINTING,2,\n",
                "A1,First,2000-01-01,SPRINTING,10.5,0.15,\n"
                        + "A1,Second,2000-01-01,SPRINTING,11.0,0.16,\n"
                        + "X1,Clash,2000-01-01,SPRINTING,11.0,0.16,\n",
                "");

        assertEquals(1, data.athletes().size());
        assertEquals("First", data.athletes().get(0).getName());
        assertEquals(2, data.skippedRows());
    }

    @Test
    void shouldSkipSessionsWithInvalidTrainingLoad() throws IOException {
        ClubData data = load("", "",
                "A1,2026-10-01T08:00,60,11\n"
                        + "A1,2026-10-01T08:00,-5,5\n"
                        + "A1,not-a-date,60,5\n"
                        + "A1,2026-10-02T08:00,60,5\n");

        assertEquals(1, data.sessions().size());
        assertEquals(3, data.skippedRows());
    }

    @Test
    void shouldFailWhenAFileIsMissing() {
        CsvDataLoader loader = new CsvDataLoader(name -> {
            throw new FileNotFoundException(name);
        }, (level, message) -> { });

        assertThrows(FileNotFoundException.class, loader::load);
    }
}
