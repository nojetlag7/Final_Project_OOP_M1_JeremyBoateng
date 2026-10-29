package com.club.athletics.service;

//one line on a leaderboard
public record RankedEntry<T>(int rank, T competitor, double score) {
}
