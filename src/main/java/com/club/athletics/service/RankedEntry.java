package com.club.athletics.service;

/** One leaderboard line. Generic so any {@code PerformanceScorable} can be ranked. */
public record RankedEntry<T>(int rank, T competitor, double score) {
}
