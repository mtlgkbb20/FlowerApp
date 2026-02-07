package com.example.flower.repository;

import com.example.flower.model.User;
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

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "users";

    public User save(User user) throws ExecutionException, InterruptedException {
        // Yeni kullanıcı için ID oluştur
        if (user.getId() == null || user.getId().isEmpty()) {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
            user.setId(docRef.getId());
        }

        // Tarih bilgilerini ayarla
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());

        // Firestore'a kaydet
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("password", user.getPassword());
        userData.put("createdAt", user.getCreatedAt().toString());
        userData.put("updatedAt", user.getUpdatedAt().toString());
        userData.put("isActive", user.getIsActive());

        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(user.getId())
                .set(userData);

        result.get(); // Kayıt tamamlanana kadar bekle
        return user;
    }

    public Optional<User> findByEmail(String email) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("email", email)
                .limit(1);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(documentToUser(documents.get(0)));
    }

    public Optional<User> findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return Optional.of(documentToUser(document));
        }

        return Optional.empty();
    }

    public Boolean existsByEmail(String email) throws ExecutionException, InterruptedException {
        return findByEmail(email).isPresent();
    }

    private User documentToUser(DocumentSnapshot document) {
        User user = new User();
        user.setId(document.getString("id"));
        user.setName(document.getString("name"));
        user.setEmail(document.getString("email"));
        user.setPassword(document.getString("password"));

        String createdAt = document.getString("createdAt");
        if (createdAt != null) {
            user.setCreatedAt(LocalDateTime.parse(createdAt));
        }

        String updatedAt = document.getString("updatedAt");
        if (updatedAt != null) {
            user.setUpdatedAt(LocalDateTime.parse(updatedAt));
        }

        user.setIsActive(document.getBoolean("isActive"));
        return user;
    }
}