package com.example.flower.service;

import com.example.flower.model.FlowerDesign;
import com.example.flower.repository.FlowerDesignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class FlowerDesignService {

    private final FlowerDesignRepository designRepository;
    private final ImageGenerationService imageGenerationService;

    public List<FlowerDesign> getDesignsByUser(String userId) throws ExecutionException, InterruptedException {
        return designRepository.findAllByUserId(userId);
    }

    @SuppressWarnings("unchecked")
    public FlowerDesign createDesign(String userId, String preferenceId, String prompt) {
        try {
            // OpenAI'dan görsel oluştur
            Map<String, Object> apiResponse = imageGenerationService.generateImage(prompt);

            // Response'tan data array'ini al
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) apiResponse.get("data");

            if (dataList == null || dataList.isEmpty()) {
                throw new RuntimeException("OpenAI'dan görsel alınamadı");
            }

            Map<String, Object> imageData = dataList.get(0);
            String imageUrl = (String) imageData.get("url");
            String revisedPrompt = (String) imageData.get("revised_prompt");

            // FlowerDesign objesi oluştur ve kaydet
            FlowerDesign design = new FlowerDesign();
            design.setUserId(userId);
            design.setPreferenceId(preferenceId);
            design.setImageUrl(imageUrl);
            design.setImagePrompt(prompt);
            design.setRevisedPrompt(revisedPrompt);

            return designRepository.save(design);

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Tasarım kaydedilirken hata oluştu: " + e.getMessage());
        }
    }

    public List<FlowerDesign> getUserDesigns(String userId) {
        try {
            return designRepository.findAllByUserId(userId);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Tasarımlar getirilirken hata oluştu: " + e.getMessage());
        }
    }
}