package com.example.tesis_proyecto.controller;

import com.example.tesis_proyecto.dto.LoginRequest;
import com.example.tesis_proyecto.dto.LoginResponse;
import com.example.tesis_proyecto.dto.RegisterRequest;
import com.example.tesis_proyecto.model.User;
import com.example.tesis_proyecto.service.AuthService;
import com.example.tesis_proyecto.service.JwtTokenProvider;
import com.example.tesis_proyecto.service.OtpService;
import com.example.tesis_proyecto.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")

public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok("Usuario registrado correctamente");
    }


    /**
     * PASO 1: Validar email y contraseña, enviar OTP
     * POST /api/auth/login/step1
     */
    @PostMapping("/login/step1")
    public ResponseEntity<?> loginStep1(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");

            if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Email y contraseña son requeridos")
                );
            }

            Optional<User> userOpt = userService.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Map.of("message", "Correo o contraseña incorrectos")
                );
            }
            User user = userOpt.get();

            if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Map.of("message", "Correo o contraseña incorrectos")
                );
            }

            if (!user.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        Map.of("message", "Usuario inactivo. Contacta al administrador.")
                );
            }

            String sessionId = otpService.createOtpSession(email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sessionId", sessionId);
            response.put("message", "Código enviado a tu correo. Válido por 10 minutos.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "Error al procesar el login: " + e.getMessage())
            );
        }
    }

    /**
     * PASO 2: Validar OTP y generar token JWT
     * POST /api/auth/login/step2
     */
    @PostMapping("/login/step2")
    public ResponseEntity<?> loginStep2(@RequestBody Map<String, String> request) {
        try {
            String sessionId = request.get("sessionId");
            String otp = request.get("otp");

            if (sessionId == null || sessionId.isEmpty() || otp == null || otp.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "SessionId y OTP son requeridos")
                );
            }

            if (!otpService.validateOtp(sessionId, otp)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Map.of("message", "Código inválido o expirado")
                );
            }

            String email = otpService.getEmailFromSession(sessionId);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        Map.of("message", "Sesión no encontrada")
                );
            }

            Optional<User> userOpt = userService.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Map.of("message", "Usuario no encontrado")
                );
            }

            User user = userOpt.get();

            String token = jwtTokenProvider.generateToken(
                    user.getId().toString(),
                    user.getEmail(),
                    user.getRole().getName()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", token);
            response.put("user", Map.of(
                    "id", user.getId().toString(),
                    "email", user.getEmail(),
                    "fullName", user.getFullName(),
                    "role", user.getRole()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "Error al validar el código: " + e.getMessage())
            );
        }
    }
}
