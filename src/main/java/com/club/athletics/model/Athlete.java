package com.club.athletics.model;

import com.club.athletics.capability.PerformanceScorable;
import com.club.athletics.capability.Trackable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class Athlete extends Person implements PerformanceScorable, Trackable {

    private final List<TrainingSession> sessions = new ArrayList<>();
    private Coach coach;

    protected Athlete(String id, String name, LocalDate dateOfBirth) {
        super(id, name, dateOfBirth);
    }

    //each subclass says which discipline it is
    //it's not stored separately, so a swimmer can't be marked as a sprinter
    public abstract Discipline getDiscipline();

    //the extra info shown on the athlete's line
    //times for sprinters, stroke for swimmers, weight for lifters
    protected abstract String describeAttributes();

    public Optional<Coach> getCoach() {
        return Optional.ofNullable(coach);
    }

    //only Coach.assign should call this so both sides stay linked
    //the athlete points at the coach and the coach's list points back
    void setCoach(Coach coach) {
        this.coach = coach;
    }

    //adding a session onto this athlete
    //it has to be for this athlete's id, not someone else's
    public void recordSession(TrainingSession session) {
        Objects.requireNonNull(session, "session");
        if (!session.getAthleteId().equals(getId())) {
            throw new IllegalArgumentException(
                    "Session belongs to " + session.getAthleteId() + ", not " + getId());
        }
        sessions.add(session);
    }

    public List<TrainingSession> getSessions() {
        return Collections.unmodifiableList(sessions);
    }

    //adding up the load between two dates, both days included
    @Override
    public int trainingLoadBetween(LocalDate fromInclusive, LocalDate toInclusive) {
        return sessions.stream()
                .filter(session -> !session.getDate().isBefore(fromInclusive)
                        && !session.getDate().isAfter(toInclusive))
                .mapToInt(TrainingSession::getLoad)
                .sum();
    }

    //finding this athlete's most recent session
    //empty if they haven't trained yet
    @Override 
    public Optional<LocalDate> latestSessionDate() {
        return sessions.stream()
                .map(TrainingSession::getDate)
                .max(Comparator.naturalOrder());
    }

    @Override
    public String getSummary() {
        String coachName = getCoach().map(Person::getName).orElse("none");
        return String.format("%-5s %-18s %-13s coach: %-16s | %s",
                getId(), getName(), getDiscipline(), coachName, describeAttributes());
    }
}
