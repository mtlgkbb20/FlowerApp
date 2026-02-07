package com.example.flower.service;

import com.example.flower.model.dto.AuthResponse;
import com.example.flower.model.dto.LoginRequest;
import com.example.flower.model.dto.RegisterRequest;
import com.example.flower.model.User;
import com.example.flower.repository.UserRepository;
import com.example.flower.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        try {
            // Email kontrolü
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Bu email zaten kayıtlı!");
            }

            // Yeni kullanıcı oluştur
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setIsActive(true);

            User savedUser = userRepository.save(user);

            // JWT token oluştur
            String token = jwtUtil.generateToken(savedUser.getEmail());

            return new AuthResponse(
                    token,
                    savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getEmail()
            );
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Kayıt işlemi sırasında hata oluştu: " + e.getMessage());
        }
    }

    public AuthResponse login(LoginRequest request) {
        try {
            // Kullanıcıyı bul
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

            // Şifre kontrolü
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("Şifre hatalı!");
            }

            // Aktiflik kontrolü
            if (!user.getIsActive()) {
                throw new RuntimeException("Hesabınız deaktif!");
            }

            // JWT token oluştur
            String token = jwtUtil.generateToken(user.getEmail());

            return new AuthResponse(
                    token,
                    user.getId(),
                    user.getName(),
                    user.getEmail()
            );
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Giriş işlemi sırasında hata oluştu: " + e.getMessage());
        }
    }
}