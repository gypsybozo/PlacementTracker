package com.placementtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDto {

    private Long id;
    
    @NotEmpty(message = "Title cannot be empty")
    private String title;
    
    private String description;
    
    private String problemUrl;
    
    @NotEmpty(message = "Difficulty level cannot be empty")
    private String difficultyLevel;
    
    @NotEmpty(message = "Platform cannot be empty")
    private String platform;
}