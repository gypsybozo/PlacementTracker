package com.placementtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroupDto {
    private Long id;
    
    @NotEmpty(message = "Group name cannot be empty")
    private String name;
    
    private String description;
    
    private String creatorUsername;
    
    private Set<String> memberUsernames;
    
    private String invitationCode;
}