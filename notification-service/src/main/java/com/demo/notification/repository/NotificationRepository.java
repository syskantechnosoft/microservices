package com.demo.notification.repository;

import com.demo.notification.domain.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationLog, Long> {
}
