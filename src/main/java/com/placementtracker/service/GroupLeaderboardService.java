package com.placementtracker.service;

import com.placementtracker.dto.LeaderboardEntryDto;
import com.placementtracker.repository.GroupLeaderboardRepository;
import com.placementtracker.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupLeaderboardService {

    private final GroupLeaderboardRepository groupLeaderboardRepository;
    private final StudyGroupRepository studyGroupRepository;

    /**
     * Get the leaderboard for a specific study group
     * @param groupId ID of the study group
     * @return List of leaderboard entries ordered by rank
     */
    public List<LeaderboardEntryDto> getGroupLeaderboard(Long groupId) {
        List<Map<String, Object>> results = groupLeaderboardRepository.getGroupLeaderboard(groupId);
        List<LeaderboardEntryDto> leaderboard = new ArrayList<>();
    
        int rank = 1;
        Long previousSolvedCount = null;
        Long previousPoints = null;
        int effectiveRank = 1;
    
        for (Map<String, Object> row : results) {
            // Use safe value extraction with null checks
            Long userId = getLongFromMap(row, "userId");
            String username = (String) row.get("username");
            Long solvedCount = getLongFromMap(row, "solvedCount");
            Long difficultyPoints = getLongFromMap(row, "difficultyPoints");
            Long easyCount = getLongFromMap(row, "easyCount");
            Long mediumCount = getLongFromMap(row, "mediumCount");
            Long hardCount = getLongFromMap(row, "hardCount");
    
            // Handle tied ranks
            if (previousSolvedCount != null && previousPoints != null) {
                if (!solvedCount.equals(previousSolvedCount) || !difficultyPoints.equals(previousPoints)) {
                    effectiveRank = rank;
                }
            }
    
            LeaderboardEntryDto entry = LeaderboardEntryDto.builder()
                    .rank(effectiveRank)
                    .userId(userId)
                    .username(username)
                    .totalSolved(solvedCount)
                    .easyCount(easyCount)
                    .mediumCount(mediumCount)
                    .hardCount(hardCount)
                    .difficultyPoints(difficultyPoints)
                    .build();
    
            leaderboard.add(entry);
            previousSolvedCount = solvedCount;
            previousPoints = difficultyPoints;
            rank++;
        }
    
        return leaderboard;
    }
    
    private Long getLongFromMap(Map<String, Object> map, String key) {
        // Safely extract the Long value, defaulting to 0 if null
        Object value = map.get(key);
        return value != null ? ((Number) value).longValue() : 0L;
    }    
    
}