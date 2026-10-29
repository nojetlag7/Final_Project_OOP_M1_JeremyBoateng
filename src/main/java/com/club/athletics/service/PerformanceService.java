package com.club.athletics.service;

import com.club.athletics.capability.PerformanceScorable;
import com.club.athletics.model.Athlete;
import com.club.athletics.model.Discipline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class PerformanceService {

    //sorting best score first, same scores share a rank
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

    //ranking only the athletes in one discipline
    public List<RankedEntry<Athlete>> rankByDiscipline(Collection<Athlete> athletes, Discipline discipline) {
        return rank(athletes.stream()
                .filter(athlete -> athlete.getDiscipline() == discipline)
                .toList());
    }
}
