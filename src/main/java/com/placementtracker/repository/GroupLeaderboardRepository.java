package com.placementtracker.repository;

import com.placementtracker.model.Problem;
import com.placementtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface GroupLeaderboardRepository extends JpaRepository<Problem, Long> {
    /**
     * Fetches leaderboard data for a specific study group.
     * Returns a list of users with their solved problems count and points based on difficulty.
     * Easy: 1 point, Medium: 2 points, Hard: 3 points
     */
    @Query(value =
        "SELECT " +
        " u.id AS userId, " +
        " u.username AS username, " +
        " COUNT(DISTINCT up.problem_id) AS solvedCount, " +
        " COALESCE(SUM(CASE " +
        "   WHEN p.difficulty_level = 'EASY' THEN 1 " +
        "   WHEN p.difficulty_level = 'MEDIUM' THEN 2 " +
        "   WHEN p.difficulty_level = 'HARD' THEN 3 " +
        "   ELSE 0 END), 0) AS difficultyPoints, " +
        " COALESCE(COUNT(CASE WHEN p.difficulty_level = 'EASY' THEN 1 ELSE NULL END), 0) AS easyCount, " +
        " COALESCE(COUNT(CASE WHEN p.difficulty_level = 'MEDIUM' THEN 1 ELSE NULL END), 0) AS mediumCount, " +
        " COALESCE(COUNT(CASE WHEN p.difficulty_level = 'HARD' THEN 1 ELSE NULL END), 0) AS hardCount " +
        "FROM users u " +
        "JOIN group_members gm ON u.id = gm.user_id " +
        "LEFT JOIN user_progress up ON u.id = up.user_id " +
        "LEFT JOIN problems p ON up.problem_id = p.id " +
        "WHERE gm.group_id = :groupId " +
        "GROUP BY u.id, u.username " +
        "ORDER BY solvedCount DESC, difficultyPoints DESC, u.username ASC",
        nativeQuery = true)

    List<Map<String, Object>> getGroupLeaderboard(Long groupId);
}