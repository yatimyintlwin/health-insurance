package com.insurance.health.service.impl;

import com.insurance.health.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    @Async
    @Override
    public void sendPolicyNotification(String to, String subject, String message) {
        log.info("=== Simulated Email Notification ===");
        log.info("To: {}", to);
        log.info("Subject: {}", subject);
        log.info("Message: {}", message);
        log.info("=== Notification simulated successfully ===");
    }
}
