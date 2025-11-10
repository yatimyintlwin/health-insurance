package com.insurance.health.scheduler;

import com.insurance.health.repository.impl.PolicyRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyStatusScheduler {

    private final PolicyRepositoryImpl policyRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void checkAndExpirePolicies() {
        LocalDate today = LocalDate.now();

        List<Map<String, AttributeValue>> items = policyRepository.findPoliciesToExpire(today);
        if (items.isEmpty()) {
            log.info("No policies to expire today.");
            return;
        }

        for (Map<String, AttributeValue> item : items) {
            String policyId = item.get("id").s();
            String endDate = item.get("endDate").s();
            String status = item.containsKey("status") ? item.get("status").s() : "ACTIVE";

            if (LocalDate.parse(endDate).isBefore(today) && !"EXPIRED".equals(status)) {
                log.info("Expiring policy {} (endDate: {})", policyId, endDate);
                policyRepository.updatePolicyStatusTransaction(policyId, "EXPIRED");
            }
        }
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void removeOldExpiredPolicies() {
        LocalDate today = LocalDate.now();
        log.info("Running scheduled cleanup for expired policies older than 3 days: {}", today);

        List<Map<String, AttributeValue>> items = policyRepository.findExpiredPoliciesToDelete(today);

        if (items.isEmpty()) {
            log.info("No old expired policies to delete today.");
            return;
        }

        for (Map<String, AttributeValue> item : items) {
            try {
                String policyId = item.get("id").s();
                String endDate = item.get("endDate").s();
                String status = item.get("status").s();
                String customerId = item.get("userId").s();

                LocalDate endDateValue = LocalDate.parse(endDate);
                if ("EXPIRED".equalsIgnoreCase(status) && endDateValue.plusDays(3).isBefore(today)) {
                    log.info("Deleting expired policy {} for customer {} (endDate: {})",
                            policyId, customerId, endDate);
                    policyRepository.delete(customerId, policyId);
                }

            } catch (Exception e) {
                log.error("Error deleting expired policy item: {}", item, e);
            }
        }
    }
}
