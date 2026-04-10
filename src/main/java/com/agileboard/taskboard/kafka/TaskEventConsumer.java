package com.agileboard.taskboard.kafka;

import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.agileboard.taskboard.entity.AuditLog;
import com.agileboard.taskboard.repository.AuditLogRepository;

@Component
public class TaskEventConsumer {

    private final AuditLogRepository auditLogRepository;

    public TaskEventConsumer(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @KafkaListener(topics = "task-events", groupId = "taskboard-group")
    public void handleTaskEvent(String message) {
        System.out.println("📬 Kafka'dan yeni mesaj geldi, veritabanına işleniyor...");

        // Gelen mesajı AuditLog tablosuna kaydediyoruz
        AuditLog log = AuditLog.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(log);

        System.out.println("✅ Olay başarıyla Audit Log olarak kaydedildi.");
    }
}
