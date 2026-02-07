package com.example.flower.repository;

import com.example.flower.model.UserPreference;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserPreferenceRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "user_preferences";

    public UserPreference save(UserPreference preference) throws ExecutionException, InterruptedException {
        // Yeni tercih için ID oluştur
        if (preference.getId() == null || preference.getId().isEmpty()) {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
            preference.setId(docRef.getId());
        }

        // Tarih bilgilerini ayarla
        if (preference.getCreatedAt() == null) {
            preference.setCreatedAt(LocalDateTime.now());
        }
        preference.setUpdatedAt(LocalDateTime.now());

        // Firestore'a kaydet
        Map<String, Object> preferenceData = new HashMap<>();
        preferenceData.put("id", preference.getId());
        preferenceData.put("userId", preference.getUserId());
        preferenceData.put("flowerTypes", preference.getFlowerTypes());
        preferenceData.put("colors", preference.getColors());
        preferenceData.put("size", preference.getSize());
        preferenceData.put("concept", preference.getConcept());
        preferenceData.put("bouquetStyle", preference.getBouquetStyle());
        preferenceData.put("cardMessage", preference.getCardMessage());
        preferenceData.put("imagePrompt", preference.getImagePrompt());
        preferenceData.put("createdAt", preference.getCreatedAt().toString());
        preferenceData.put("updatedAt", preference.getUpdatedAt().toString());

        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(preference.getId())
                .set(preferenceData);

        result.get();
        return preference;
    }

    public Optional<UserPreference> findLatestByUserId(String userId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(1);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(documentToPreference(documents.get(0)));
    }

    public List<UserPreference> findAllByUserId(String userId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        return documents.stream()
                .map(this::documentToPreference)
                .collect(Collectors.toList());
    }

    private UserPreference documentToPreference(DocumentSnapshot document) {
        UserPreference preference = new UserPreference();
        preference.setId(document.getString("id"));
        preference.setUserId(document.getString("userId"));
        preference.setFlowerTypes((List<String>) document.get("flowerTypes"));
        preference.setColors((List<String>) document.get("colors"));
        preference.setSize(document.getString("size"));
        preference.setConcept(document.getString("concept"));
        preference.setBouquetStyle(document.getString("bouquetStyle"));
        preference.setCardMessage(document.getString("cardMessage"));

        String createdAt = document.getString("createdAt");
        if (createdAt != null) {
            preference.setCreatedAt(LocalDateTime.parse(createdAt));
        }

        String updatedAt = document.getString("updatedAt");
        if (updatedAt != null) {
            preference.setUpdatedAt(LocalDateTime.parse(updatedAt));
        }

        return preference;
    }
}