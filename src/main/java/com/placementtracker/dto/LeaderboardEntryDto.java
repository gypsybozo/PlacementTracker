package com.placementtracker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardEntryDto {
    private int rank;
    private Long userId;
    private String username;
    private Long totalSolved;
    private Long easyCount;
    private Long mediumCount;
    private Long hardCount;
    private Long difficultyPoints;
}