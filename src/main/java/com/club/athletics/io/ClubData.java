package com.club.athletics.io;

import com.club.athletics.model.Athlete;
import com.club.athletics.model.Coach;
import com.club.athletics.model.TrainingSession;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Raw result of loading. Assignments and sessions are kept as requests rather than applied,
 * because applying them means enforcing business rules, which is the services' job.
 *
 * @param requestedAssignments athlete id to coach id, as written in the data file
 * @param skippedRows          number of rows rejected while parsing
 */
public record ClubData(List<Athlete> athletes,
                       List<Coach> coaches,
                       Map<String, String> requestedAssignments,
                       List<TrainingSession> sessions,
                       int skippedRows) {

    public ClubData {
        athletes = List.copyOf(athletes);
        coaches = List.copyOf(coaches);
        // File order decides who gets a coach's last free slot, so it must be preserved.
        requestedAssignments = Collections.unmodifiableMap(new LinkedHashMap<>(requestedAssignments));
        sessions = List.copyOf(sessions);
    }
}
