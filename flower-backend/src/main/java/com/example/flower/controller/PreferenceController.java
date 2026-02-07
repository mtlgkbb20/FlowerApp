package com.example.flower.controller;

import com.example.flower.model.dto.UserPreferenceRequest;
import com.example.flower.model.UserPreference;
import com.example.flower.security.JWTUtil;
import com.example.flower.service.PreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed.origins}")
public class PreferenceController {

    private final PreferenceService preferenceService;
    private final JWTUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> savePreference(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserPreferenceRequest request) {
        try {
            // Token'dan userId al
            String token = authHeader.substring(7); // "Bearer " kısmını çıkar
            String email = jwtUtil.getEmailFromToken(token);

            // Basit bir şekilde email'i userId olarak kullanıyoruz
            // Gerçek uygulamada user repository'den userId alınmalı

            UserPreference savedPreference = preferenceService.savePreference(email, request);

            // Prompt oluştur
            String imagePrompt = preferenceService.generateImagePrompt(savedPreference);

            Map<String, Object> response = new HashMap<>();
            response.put("preference", savedPreference);
            response.put("imagePrompt", imagePrompt);
            response.put("message", "Tercihleriniz başarıyla kaydedildi!");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestPreference(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getEmailFromToken(token);

            UserPreference preference = preferenceService.getLatestPreference(email);

            return ResponseEntity.ok(preference);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPreferences(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getEmailFromToken(token);

            List<UserPreference> preferences = preferenceService.getAllPreferences(email);

            return ResponseEntity.ok(preferences);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/generate-prompt")
    public ResponseEntity<?> generatePrompt(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserPreferenceRequest request) {
        try {
            // Geçici preference objesi oluştur (kaydetmeden)
            UserPreference tempPreference = new UserPreference();
            tempPreference.setFlowerTypes(request.getFlowerTypes());
            tempPreference.setColors(request.getColors());
            tempPreference.setSize(request.getSize());
            tempPreference.setConcept(request.getConcept());
            tempPreference.setBouquetStyle(request.getBouquetStyle());
            tempPreference.setCardMessage(request.getCardMessage());

            String imagePrompt = preferenceService.generateImagePrompt(tempPreference);

            Map<String, String> response = new HashMap<>();
            response.put("prompt", imagePrompt);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}