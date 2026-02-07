package com.example.flower.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowerDesign {

    private String id;
    private String userId;
    private String preferenceId;
    private String imageUrl;
    private String imagePrompt;
    private String revisedPrompt;
    private LocalDateTime createdAt;
}