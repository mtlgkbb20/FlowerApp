package com.example.flower.controller;

import com.example.flower.model.FlowerDesign;
import com.example.flower.security.JWTUtil;
import com.example.flower.service.FlowerDesignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/designs")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed.origins}")
public class DesignController {

    private final FlowerDesignService designService;
    private final JWTUtil jwtUtil;

    @GetMapping("/designs")
    public ResponseEntity<?> getUserDesigns(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getEmailFromToken(token);

            List<FlowerDesign> designs = designService.getDesignsByUser(email);
            return ResponseEntity.ok(designs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateDesign(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getEmailFromToken(token);

            String prompt = request.get("prompt");
            String preferenceId = request.get("preferenceId");

            if (prompt == null || prompt.isEmpty()) {
                return ResponseEntity.badRequest().body("Prompt boş olamaz!");
            }

            FlowerDesign design = designService.createDesign(email, preferenceId, prompt);

            return ResponseEntity.ok(design);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-designs")
    public ResponseEntity<?> getMyDesigns(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getEmailFromToken(token);

            List<FlowerDesign> designs = designService.getUserDesigns(email);

            return ResponseEntity.ok(designs);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
