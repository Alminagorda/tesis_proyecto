package com.example.tesis_proyecto.model;

import lombok.Data;

import java.util.List;
@Data
public class BehaviorProfile {
    private String       username;
    private List<Integer> usualLoginHours;
    private List<String> usualIPs;
    private double       averageSessionDuration;
    private int          loginFrequency;
    private double       riskScore;
}
