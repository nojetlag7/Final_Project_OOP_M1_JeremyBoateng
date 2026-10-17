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

    /** Derived from the concrete type so an athlete can never disagree with its own class. */
    public abstract Discipline getDiscipline();

    /** Discipline-specific attributes, used in summaries. */
    protected abstract String describeAttributes();

    public Optional<Coach> getCoach() {
        return Optional.ofNullable(coach);
    }

    // Package-private: only Coach.assign may change the link, so both sides stay consistent.
    void setCoach(Coach coach) {
        this.coach = coach;
    }

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

    @Override
    public int trainingLoadBetween(LocalDate fromInclusive, LocalDate toInclusive) {
        return sessions.stream()
                .filter(session -> !session.getDate().isBefore(fromInclusive)
                        && !session.getDate().isAfter(toInclusive))
                .mapToInt(TrainingSession::getLoad)
                .sum();
    }

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
