package com.taskflow.notification.repository;

import com.taskflow.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String email);
    long countByRecipientEmailAndReadFalse(String email);
    Optional<Notification> findByIdAndRecipientEmail(Long id, String email);
}