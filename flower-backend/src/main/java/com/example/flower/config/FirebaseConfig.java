package com.example.flower.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.config.path:src/main/resources/firebase-service-account.json}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options;

            // Railway ortamında FIREBASE_SERVICE_ACCOUNT_BASE64 environment variable'ı varsa onu kullan
            String base64Credentials = System.getenv("FIREBASE_SERVICE_ACCOUNT_BASE64");

            if (base64Credentials != null && !base64Credentials.isEmpty()) {
                System.out.println("Firebase: Environment variable'dan kimlik yükleniyor...");
                byte[] decoded = Base64.getDecoder().decode(base64Credentials);
                GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(decoded));

                options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .build();
            } else {
                System.out.println("Firebase: Local JSON dosyasından kimlik yükleniyor...");
                FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);

                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
            }

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase başarıyla başlatıldı!");
            }

        } catch (IOException e) {
            System.err.println("❌ Firebase başlatma hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }
}
