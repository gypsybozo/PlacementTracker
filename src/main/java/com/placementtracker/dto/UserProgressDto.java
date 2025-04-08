package com.placementtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressDto {

    private Long id;
    
    private Long problemId;
    
    @NotEmpty(message = "Problem title cannot be empty")
    private String problemTitle;
    
    @NotEmpty(message = "Platform cannot be empty")
    private String platform;
    
    @NotEmpty(message = "Difficulty level cannot be empty")
    private String difficultyLevel;
    
    private String problemUrl;
    
    private String solution;
    
    private String languageUsed;
    
    private Integer timeTakenMinutes;
    
    private String notes;
}