package com.example.tesis_proyecto.service;

import com.example.tesis_proyecto.dto.LoginRequest;
import com.example.tesis_proyecto.dto.LoginResponse;
import com.example.tesis_proyecto.dto.RegisterRequest;
import com.example.tesis_proyecto.model.User;
import com.example.tesis_proyecto.repository.UserRepository;
import com.example.tesis_proyecto.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public LoginResponse login(LoginRequest req) {

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 🔒 Verificar bloqueo
        if (user.getLockedUntil() != null &&
                user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Cuenta bloqueada temporalmente");
        }

        // 🔑 Verificar contraseña
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {

            user.setFailedAttempts(user.getFailedAttempts() + 1);

            // bloquear después de 5 intentos
            if (user.getFailedAttempts() >= 5) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
            }

            userRepository.save(user);
            throw new RuntimeException("Credenciales incorrectas");
        }

        // ✅ login correcto
        user.setFailedAttempts(0);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        //return new LoginResponse(token, user.getRole(), user.getFullName());
        return new LoginResponse(token, user);
    }
    public void register(RegisterRequest req) {

        // verificar si ya existe
        Optional<User> existingUser = userRepository.findByEmail(req.getEmail());

        if (existingUser.isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        // crear usuario
        User user = new User();
        user.setEmail(req.getEmail());
        user.setFullName(req.getFullName());

        // 🔐 IMPORTANTE: encriptar contraseña
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));

        user.setRole("soc-analyst");
        user.setIsActive(true);
        user.setFailedAttempts(0);

        userRepository.save(user);
    }
}
