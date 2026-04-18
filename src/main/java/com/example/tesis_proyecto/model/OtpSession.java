package com.example.tesis_proyecto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@RedisHash("OtpSession")
public class OtpSession {
    @Id
    private String sessionId;

    private String email;
    private String otp;
    private LocalDateTime expiresAt;
    private boolean used;

    public OtpSession() {
    }

    public OtpSession(String sessionId, String email, String otp, LocalDateTime expiresAt) {
        this.sessionId = sessionId;
        this.email = email;
        this.otp = otp;
        this.expiresAt = expiresAt;
        this.used = false;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
