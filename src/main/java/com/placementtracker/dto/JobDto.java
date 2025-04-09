package com.placementtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDto {
    private Long id;
    
    @NotEmpty(message = "Job title is required")
    private String title;
    
    @NotEmpty(message = "Company name is required")
    private String company;
    
    @NotEmpty(message = "Location is required")
    private String location;
    
    private String description;
    
    private String skillsRequired;
    
    private Integer experienceYears;
    
    private LocalDateTime postedDate;
    
    private LocalDateTime applicationDeadline;
    
    private String salaryRange;
    
    private Set<String> tags = new HashSet<>();
    
    private boolean active = true;
}