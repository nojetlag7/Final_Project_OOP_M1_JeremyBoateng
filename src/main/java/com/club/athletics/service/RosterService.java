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

//holds the athletes and coaches and the things you can do with them
//lookups, assigning a coach, and logging a session
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

    //getting the athlete, or throwing if the id doesn't exist
    //used when the menu needs a real athlete, not an empty result
    public Athlete getAthleteOrThrow(String id) throws AthleteNotFoundException {
        return findAthlete(id).orElseThrow(() -> new AthleteNotFoundException(id));
    }

    //getting the coach, or throwing if the id doesn't exist
    //same idea as getAthleteOrThrow
    public Coach getCoachOrThrow(String id) throws CoachNotFoundException {
        return findCoach(id).orElseThrow(() -> new CoachNotFoundException(id));
    }

    //getting athletes that don't have a coach yet
    //shown before you assign someone
    public List<Athlete> getUnassignedAthletes() {
        return athletesById.values().stream()
                .filter(athlete -> athlete.getCoach().isEmpty())
                .toList();
    }

    //getting the coaches that can take this athlete right now
    //discipline, free spots and certification all have to pass
    public List<Coach> findEligibleCoaches(Athlete athlete) {
        return coachesById.values().stream()
                .filter(coach -> canAssign(coach, athlete))
                .toList();
    }

    //check to see if this coach would accept the athlete
    //same checks as assigning, but it doesn't throw, just yes or no
    private static boolean canAssign(Coach coach, Athlete athlete) {
        try {
            coach.validateAssignment(athlete);
            return true;
        } catch (InvalidAssignmentException e) {
            return false;
        }
    }

    //assigning a coach to an athlete using their ids
    //throws if either id is wrong or the coach can't take them
    public Coach assignCoach(String athleteId, String coachId)
            throws AthleteNotFoundException, CoachNotFoundException, InvalidAssignmentException {
        Athlete athlete = getAthleteOrThrow(athleteId);
        Coach coach = getCoachOrThrow(coachId);
        coach.assign(athlete);
        return coach;
    }

    //saving the session if it doesn't overlap another one
    //the athlete has to exist, and they can't already be training at that time
    public void logSession(TrainingSession session) throws AthleteNotFoundException, SchedulingConflictException {
        Athlete athlete = getAthleteOrThrow(session.getAthleteId());
        scheduling.checkConflict(session, athlete.getSessions());
        athlete.recordSession(session);
    }
}
