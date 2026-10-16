package com.club.athletics.capability;

/** Anything that can be placed on a leaderboard. Higher score means better performance. */
public interface PerformanceScorable {

    double calculatePerformanceScore();
}
