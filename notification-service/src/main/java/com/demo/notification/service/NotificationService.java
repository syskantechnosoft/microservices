package com.demo.notification.service;

import com.demo.notification.domain.NotificationLog;
import com.demo.notification.dto.NotificationSentEvent;
import com.demo.notification.dto.PaymentCompletedEvent;
import com.demo.notification.repository.NotificationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public NotificationService(NotificationRepository notificationRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.notificationRepository = notificationRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "payment-completed", groupId = "notification-service")
    public void consume(PaymentCompletedEvent event) {
        NotificationLog log = new NotificationLog();
        log.setOrderId(event.orderId());
        log.setUsername(event.username());
        log.setMessage("Payment completed for order " + event.orderId());
        log.setStatus("SENT");
        log.setSentAt(Instant.now());
        notificationRepository.save(log);

        kafkaTemplate.send("notification-sent", new NotificationSentEvent(
                event.orderId(), event.username(), "SENT", log.getSentAt()));
    }

    public List<NotificationLog> findAll() {
        return notificationRepository.findAll();
    }
}
