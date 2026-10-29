package com.club.athletics.io;

import com.club.athletics.model.Athlete;
import com.club.athletics.model.Coach;
import com.club.athletics.model.TrainingSession;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//what came out of the files, coaches aren't assigned yet
public record ClubData(List<Athlete> athletes,
                       List<Coach> coaches,
                       Map<String, String> requestedAssignments,
                       List<TrainingSession> sessions,
                       int skippedRows) {

    public ClubData {
        athletes = List.copyOf(athletes);
        coaches = List.copyOf(coaches);
        //keeping the file order so the first athletes get the last free spots
        requestedAssignments = Collections.unmodifiableMap(new LinkedHashMap<>(requestedAssignments));
        sessions = List.copyOf(sessions);
    }
}
