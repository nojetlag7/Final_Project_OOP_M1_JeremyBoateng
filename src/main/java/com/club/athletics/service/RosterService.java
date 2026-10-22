package com.club.athletics.service;

import com.club.athletics.exception.AthleteNotFoundException;
import com.club.athletics.exception.CoachNotFoundException;
import com.club.athletics.exception.InvalidAssignmentException;
import com.club.athletics.exception.SchedulingConflictException;
import com.club.athletics.model.Athlete;
import com.club.athletics.model.Coach;
import com.club.athletics.model.Person;
import com.club.athletics.model.TrainingSession;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** The club's roster: lookups, coach assignment and session logging. */
public class RosterService {

    private final Map<String, Athlete> athletesById;
    private final Map<String, Coach> coachesById;
    private final SchedulingService scheduling;

    public RosterService(Collection<Athlete> athletes, Collection<Coach> coaches) {
        this(athletes, coaches, new SchedulingService());
    }

    public RosterService(Collection<Athlete> athletes, Collection<Coach> coaches, SchedulingService scheduling) {
        this.athletesById = indexById(athletes);
        this.coachesById = indexById(coaches);
        this.scheduling = Objects.requireNonNull(scheduling, "scheduling");
    }

    private static <T extends Person> Map<String, T> indexById(Collection<T> people) {
        Map<String, T> index = new LinkedHashMap<>();
        for (T person : people) {
            if (index.putIfAbsent(person.getId(), person) != null) {
                throw new IllegalArgumentException("Duplicate id " + person.getId());
            }
        }
        return index;
    }

    public List<Athlete> getAthletes() {
        return List.copyOf(athletesById.values());
    }

    public List<Coach> getCoaches() {
        return List.copyOf(coachesById.values());
    }

    public Optional<Athlete> findAthlete(String id) {
        return Optional.ofNullable(athletesById.get(id));
    }

    public Optional<Coach> findCoach(String id) {
        return Optional.ofNullable(coachesById.get(id));
    }

    public Athlete getAthleteOrThrow(String id) throws AthleteNotFoundException {
        return findAthlete(id).orElseThrow(() -> new AthleteNotFoundException(id));
    }

    public Coach getCoachOrThrow(String id) throws CoachNotFoundException {
        return findCoach(id).orElseThrow(() -> new CoachNotFoundException(id));
    }

    public List<Athlete> getUnassignedAthletes() {
        return athletesById.values().stream()
                .filter(athlete -> athlete.getCoach().isEmpty())
                .toList();
    }

    /** Coaches that would accept this athlete right now, for suggesting alternatives. */
    public List<Coach> findEligibleCoaches(Athlete athlete) {
        return coachesById.values().stream()
                .filter(coach -> canAssign(coach, athlete))
                .toList();
    }

    private static boolean canAssign(Coach coach, Athlete athlete) {
        try {
            coach.validateAssignment(athlete);
            return true;
        } catch (InvalidAssignmentException e) {
            return false;
        }
    }

    public Coach assignCoach(String athleteId, String coachId)
            throws AthleteNotFoundException, CoachNotFoundException, InvalidAssignmentException {
        Athlete athlete = getAthleteOrThrow(athleteId);
        Coach coach = getCoachOrThrow(coachId);
        coach.assign(athlete);
        return coach;
    }

    public void logSession(TrainingSession session) throws AthleteNotFoundException, SchedulingConflictException {
        Athlete athlete = getAthleteOrThrow(session.getAthleteId());
        scheduling.checkConflict(session, athlete.getSessions());
        athlete.recordSession(session);
    }
}
