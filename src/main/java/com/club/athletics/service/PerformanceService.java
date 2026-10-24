package com.club.athletics.service;

import com.club.athletics.capability.PerformanceScorable;
import com.club.athletics.model.Athlete;
import com.club.athletics.model.Discipline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class PerformanceService {

    /**
     * Ranks by score, best first. Equal scores share a rank and the next rank is skipped
     * (1, 1, 3), the usual competition convention.
     */
    public <T extends PerformanceScorable> List<RankedEntry<T>> rank(Collection<T> competitors) {
        List<T> sorted = competitors.stream()
                .sorted(Comparator.comparingDouble((T c) -> c.calculatePerformanceScore()).reversed())
                .toList();

        List<RankedEntry<T>> leaderboard = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            T competitor = sorted.get(i);
            double score = competitor.calculatePerformanceScore();
            int rank = i + 1;
            if (i > 0 && Double.compare(score, leaderboard.get(i - 1).score()) == 0) {
                rank = leaderboard.get(i - 1).rank();
            }
            leaderboard.add(new RankedEntry<>(rank, competitor, score));
        }
        return leaderboard;
    }

    public List<RankedEntry<Athlete>> rankByDiscipline(Collection<Athlete> athletes, Discipline discipline) {
        return rank(athletes.stream()
                .filter(athlete -> athlete.getDiscipline() == discipline)
                .toList());
    }
}
