package com.insurance.health.service;

public interface EmailService {
    void sendPolicyNotification(String to, String subject, String message);
}
