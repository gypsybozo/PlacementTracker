package com.placementtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionCommentDto {
    private Long id;
    
    private Long discussionId;
    
    private String username;
    
    @NotEmpty(message = "Comment content cannot be empty")
    private String content;
    
    private LocalDateTime createdAt;
}