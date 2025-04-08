package com.placementtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDiscussionDto {
    private Long id;
    
    private Long groupId;
    
    private String username;
    
    private Long problemId;
    
    @NotEmpty(message = "Title cannot be empty")
    private String title;
    
    @NotEmpty(message = "Content cannot be empty")
    private String content;
    
    private LocalDateTime createdAt;
}