package com.example.flower.repository;

import com.example.flower.model.FlowerDesign;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FlowerDesignRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "flower_designs";

    public FlowerDesign save(FlowerDesign design) throws ExecutionException, InterruptedException {
        if (design.getId() == null || design.getId().isEmpty()) {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
            design.setId(docRef.getId());
        }

        if (design.getCreatedAt() == null) {
            design.setCreatedAt(LocalDateTime.now());
        }

        Map<String, Object> designData = new HashMap<>();
        designData.put("id", design.getId());
        designData.put("userId", design.getUserId());
        designData.put("preferenceId", design.getPreferenceId());
        designData.put("imageUrl", design.getImageUrl());
        designData.put("imagePrompt", design.getImagePrompt());
        designData.put("revisedPrompt", design.getRevisedPrompt());
        designData.put("createdAt", design.getCreatedAt().toString());

        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(design.getId())
                .set(designData);

        result.get();
        return design;
    }

    public List<FlowerDesign> findAllByUserId(String userId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        return documents.stream()
                .map(this::documentToDesign)
                .collect(Collectors.toList());
    }

    private FlowerDesign documentToDesign(DocumentSnapshot document) {
        FlowerDesign design = new FlowerDesign();
        design.setId(document.getString("id"));
        design.setUserId(document.getString("userId"));
        design.setPreferenceId(document.getString("preferenceId"));
        design.setImageUrl(document.getString("imageUrl"));
        design.setImagePrompt(document.getString("imagePrompt"));
        design.setRevisedPrompt(document.getString("revisedPrompt"));

        String createdAt = document.getString("createdAt");
        if (createdAt != null) {
            design.setCreatedAt(LocalDateTime.parse(createdAt));
        }

        return design;
    }

    public void saveDesign(FlowerDesign design) {
        try {
            DocumentReference docRef = firestore.collection("flower_designs").document();
            design.setId(docRef.getId()); // opsiyonel: Firestore ID’yi modele koymak istersen
            ApiFuture<WriteResult> result = docRef.set(design);
            result.get(); // async işlemi blokla, ya da logla
        } catch (Exception e) {
            throw new RuntimeException("Firebase'e kaydedilemedi: " + e.getMessage());
        }
    }
}