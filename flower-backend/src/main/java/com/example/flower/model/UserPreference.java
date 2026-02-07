package com.example.flower.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {

    private String id;
    private String userId;
    private List<String> flowerTypes;
    private List<String> colors;
    private String size;
    private String concept;
    private String bouquetStyle;
    private String cardMessage;
    private String imagePrompt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
