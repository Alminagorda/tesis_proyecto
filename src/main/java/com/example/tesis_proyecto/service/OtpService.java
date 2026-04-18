package com.example.tesis_proyecto.service;

import com.example.tesis_proyecto.model.OtpSession;
import com.example.tesis_proyecto.repository.OtpSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class OtpService {

    @Autowired
    private OtpSessionRepository otpSessionRepository;

    @Autowired
    private EmailService emailService;

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public String createOtpSession(String email) throws Exception {
        String otp = generateOtp();
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        OtpSession otpSession = new OtpSession(sessionId, email, otp, expiresAt);
        otpSessionRepository.save(otpSession);

        emailService.sendOtpEmail(email, otp);

        return sessionId;
    }

    public boolean validateOtp(String sessionId, String otp) {
        var optionalSession = otpSessionRepository.findById(sessionId);

        if (optionalSession.isEmpty()) {
            return false;
        }

        OtpSession otpSession = optionalSession.get();

        if (otpSession.isExpired()) {
            otpSessionRepository.delete(otpSession);
            return false;
        }

        if (otpSession.isUsed()) {
            return false;
        }

        if (!otpSession.getOtp().equals(otp)) {
            return false;
        }

        otpSession.setUsed(true);
        otpSessionRepository.save(otpSession);

        return true;
    }

    public String getEmailFromSession(String sessionId) {
        var optionalSession = otpSessionRepository.findById(sessionId);
        if (optionalSession.isEmpty()) {
            return null;
        }
        return optionalSession.get().getEmail();
    }
}
