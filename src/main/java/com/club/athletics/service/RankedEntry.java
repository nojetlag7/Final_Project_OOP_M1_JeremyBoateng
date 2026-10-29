package com.club.athletics.service;

//one line on a leaderboard: the place, the person and their score
public record RankedEntry<T>(int rank, T competitor, double score) {
}
