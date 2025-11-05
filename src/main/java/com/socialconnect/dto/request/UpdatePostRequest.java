package com.socialconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePostRequest {
    
    @NotBlank(message = "Content is required")
    private String content;
}

