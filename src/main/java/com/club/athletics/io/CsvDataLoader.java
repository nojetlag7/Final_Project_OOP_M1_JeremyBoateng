package com.club.athletics.io;

import com.club.athletics.exception.AthleteManagementException;
import com.club.athletics.exception.DataFormatException;
import com.club.athletics.model.Athlete;
import com.club.athletics.model.Coach;
import com.club.athletics.model.Discipline;
import com.club.athletics.model.SprintCoach;
import com.club.athletics.model.Sprinter;
import com.club.athletics.model.Stroke;
import com.club.athletics.model.SwimCoach;
import com.club.athletics.model.Swimmer;
import com.club.athletics.model.TrainingSession;
import com.club.athletics.model.WeightliftingCoach;
import com.club.athletics.model.Weightlifter;
import com.club.athletics.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

//reading the csv files, a bad row gets skipped but a missing file stops the app
public class CsvDataLoader implements DataLoader {

    public static final String COACHES_FILE = "coaches.csv";
    public static final String ATHLETES_FILE = "athletes.csv";
    public static final String SESSIONS_FILE = "sessions.csv";

    private static final int COACH_COLUMNS = 6;
    private static final int ATHLETE_COLUMNS = 7;
    private static final int SESSION_COLUMNS = 4;

    private final CsvSource source;
    private final Logger logger;
    private int skippedRows;

    public CsvDataLoader(CsvSource source, Logger logger) {
        this.source = Objects.requireNonNull(source, "source");
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    @FunctionalInterface
    private interface RowParser<T> {
        T parse(String[] columns) throws AthleteManagementException;
    }

    //loading coaches, athletes and sessions
    @Override
    public ClubData load() throws IOException {
        skippedRows = 0;
        Set<String> seenIds = new HashSet<>();
        Map<String, String> requestedAssignments = new LinkedHashMap<>();

        List<Coach> coaches = readRows(COACHES_FILE, COACH_COLUMNS, columns -> {
            Coach coach = parseCoach(columns);
            claimId(seenIds, coach.getId());
            return coach;
        });
        List<Athlete> athletes = readRows(ATHLETES_FILE, ATHLETE_COLUMNS, columns -> {
            Athlete athlete = parseAthlete(columns);
            claimId(seenIds, athlete.getId());
            if (!columns[6].isEmpty()) {
                requestedAssignments.put(athlete.getId(), columns[6]);
            }
            return athlete;
        });
        List<TrainingSession> sessions = readRows(SESSIONS_FILE, SESSION_COLUMNS, this::parseSession);

        logger.info(String.format("Loaded %d coaches, %d athletes, %d sessions (%d rows skipped)",
                coaches.size(), athletes.size(), sessions.size(), skippedRows));
        return new ClubData(athletes, coaches, requestedAssignments, sessions, skippedRows);
    }

    //reading one file and skipping lines that don't parse
    private <T> List<T> readRows(String fileName, int expectedColumns, RowParser<T> parser) throws IOException {
        List<T> results = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(source.open(fileName))) {
            if (reader.readLine() == null) {
                logger.warn(fileName + " is empty");
                return results;
            }
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                try {
                    results.add(parser.parse(split(line, expectedColumns)));
                } catch (AthleteManagementException | IllegalArgumentException e) {
                    // IllegalArgumentException comes from model constructors rejecting values.
                    skippedRows++;
                    logger.warn(String.format("%s line %d skipped: %s", fileName, lineNumber, e.getMessage()));
                }
            }
        }
        return results;
    }

    //splitting a line and checking it has the right number of columns
    private static String[] split(String line, int expectedColumns) throws DataFormatException {
        String[] columns = line.split(",", -1);
        if (columns.length != expectedColumns) {
            throw new DataFormatException(String.format("expected %d columns but found %d",
                    expectedColumns, columns.length));
        }
        for (int i = 0; i < columns.length; i++) {
            columns[i] = columns[i].trim();
        }
        return columns;
    }

    //making sure this id hasn't already been used
    private static void claimId(Set<String> seenIds, String id) throws DataFormatException {
        if (!seenIds.add(id)) {
            throw new DataFormatException("duplicate id '" + id + "'");
        }
    }

    //building the right coach from the discipline column
    private Coach parseCoach(String[] c) throws DataFormatException {
        LocalDate dateOfBirth = parseDate(c[2], "dateOfBirth");
        Discipline discipline = parseEnum(Discipline.class, c[3], "discipline");
        int maxAthletes = parseInt(c[4], "maxAthletes");
        return switch (discipline) {
            case SPRINTING -> new SprintCoach(c[0], c[1], dateOfBirth, maxAthletes);
            case SWIMMING -> new SwimCoach(c[0], c[1], dateOfBirth, maxAthletes, parseStrokes(c[5]));
            case WEIGHTLIFTING -> new WeightliftingCoach(c[0], c[1], dateOfBirth, maxAthletes,
                    parseDouble(c[5], "max weight class"));
        };
    }

    //building the right athlete from the discipline column
    private Athlete parseAthlete(String[] c) throws DataFormatException {
        LocalDate dateOfBirth = parseDate(c[2], "dateOfBirth");
        Discipline discipline = parseEnum(Discipline.class, c[3], "discipline");
        return switch (discipline) {
            case SPRINTING -> new Sprinter(c[0], c[1], dateOfBirth,
                    parseDouble(c[4], "personal best"), parseDouble(c[5], "reaction time"));
            case SWIMMING -> new Swimmer(c[0], c[1], dateOfBirth,
                    parseEnum(Stroke.class, c[4], "stroke"), parseDouble(c[5], "lap time"));
            case WEIGHTLIFTING -> new Weightlifter(c[0], c[1], dateOfBirth,
                    parseDouble(c[4], "weight class"), parseDouble(c[5], "total"));
        };
    }

    //building a session from one row
    private TrainingSession parseSession(String[] c) throws AthleteManagementException {
        LocalDateTime start;
        try {
            start = LocalDateTime.parse(c[1]);
        } catch (DateTimeParseException e) {
            throw new DataFormatException("invalid dateTime '" + c[1] + "'", e);
        }
        return new TrainingSession(c[0], start, parseInt(c[2], "durationMinutes"), parseInt(c[3], "intensity"));
    }

    private static Set<Stroke> parseStrokes(String value) throws DataFormatException {
        Set<Stroke> strokes = EnumSet.noneOf(Stroke.class);
        for (String part : value.split("\\|")) {
            strokes.add(parseEnum(Stroke.class, part.trim(), "stroke"));
        }
        return strokes;
    }

    private static LocalDate parseDate(String value, String field) throws DataFormatException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new DataFormatException("invalid " + field + " '" + value + "'", e);
        }
    }

    private static int parseInt(String value, String field) throws DataFormatException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new DataFormatException("invalid " + field + " '" + value + "'", e);
        }
    }

    private static double parseDouble(String value, String field) throws DataFormatException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new DataFormatException("invalid " + field + " '" + value + "'", e);
        }
    }

    private static <E extends Enum<E>> E parseEnum(Class<E> type, String value, String field)
            throws DataFormatException {
        try {
            return Enum.valueOf(type, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DataFormatException("invalid " + field + " '" + value + "'", e);
        }
    }
}
