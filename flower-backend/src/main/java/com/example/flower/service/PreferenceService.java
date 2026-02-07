package com.example.flower.service;

import com.example.flower.model.dto.UserPreferenceRequest;
import com.example.flower.model.UserPreference;
import com.example.flower.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class PreferenceService {

    private final UserPreferenceRepository preferenceRepository;

    public UserPreference savePreference(String userId, UserPreferenceRequest request) {
        try {
            UserPreference preference = new UserPreference();
            preference.setUserId(userId);
            preference.setFlowerTypes(request.getFlowerTypes());
            preference.setColors(request.getColors());
            preference.setSize(request.getSize());
            preference.setConcept(request.getConcept());
            preference.setBouquetStyle(request.getBouquetStyle());
            preference.setCardMessage(request.getCardMessage());

            // Prompt'u oluştur ve preference'a ekle
            String imagePrompt = generateImagePrompt(preference);
            preference.setImagePrompt(imagePrompt);

            return preferenceRepository.save(preference);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Tercihler kaydedilirken hata oluştu: " + e.getMessage());
        }
    }

    public UserPreference getLatestPreference(String userId) {
        try {
            return preferenceRepository.findLatestByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Kullanıcıya ait tercih bulunamadı"));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Tercihler getirilirken hata oluştu: " + e.getMessage());
        }
    }

    public List<UserPreference> getAllPreferences(String userId) {
        try {
            return preferenceRepository.findAllByUserId(userId);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Tercihler getirilirken hata oluştu: " + e.getMessage());
        }
    }

    public String generateImagePrompt(UserPreference preference) {
        StringBuilder prompt = new StringBuilder();

        // Çiçek türleri
        prompt.append("A beautiful flower bouquet featuring ");
        prompt.append(String.join(", ", preference.getFlowerTypes()));

        // Renkler
        prompt.append(" in ");
        prompt.append(String.join(", ", preference.getColors()));
        prompt.append(" colors");

        // Boyut
        String sizeDescription = switch (preference.getSize()) {
            case "small" -> "small and delicate";
            case "medium" -> "medium-sized";
            case "large" -> "large and impressive";
            case "extra" -> "extra large and luxurious";
            default -> "medium-sized";
        };
        prompt.append(", ").append(sizeDescription);

        // Konsept
        String conceptDescription = switch (preference.getConcept()) {
            case "romantic" -> "romantic and dreamy atmosphere";
            case "elegant" -> "elegant and sophisticated style";
            case "modern" -> "modern and contemporary design";
            case "rustic" -> "rustic and natural charm";
            case "luxurious" -> "luxurious and opulent presentation";
            case "minimalist" -> "minimalist and clean aesthetic";
            case "colorful" -> "vibrant and colorful arrangement";
            case "natural" -> "natural and organic look";
            default -> "beautiful arrangement";
        };
        prompt.append(", with ").append(conceptDescription);

        // Buket stili
        String styleDescription = switch (preference.getBouquetStyle()) {
            case "round" -> "arranged in a classic round bouquet style";
            case "cascade" -> "arranged in an elegant cascading style";
            case "hand-tied" -> "hand-tied with natural stems showing";
            case "basket" -> "beautifully arranged in a rustic basket";
            case "box" -> "elegantly presented in a luxury box";
            case "vase" -> "artistically arranged in a decorative vase";
            default -> "beautifully arranged";
        };
        prompt.append(", ").append(styleDescription);

        // Genel eklemeler
        prompt.append(". Professional photography, soft natural lighting, high quality, detailed, ");
        prompt.append("studio shot, white background, fresh flowers, perfect composition.");

        return prompt.toString();
    }
}