package com.taskflow.notification.controller;

import com.taskflow.notification.model.Notification;
import com.taskflow.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    public record NotificationResponse(Long id, String message, Long projectId,
                                       boolean read, LocalDateTime createdAt) {}

    private final NotificationRepository repository;

    @GetMapping
    public List<NotificationResponse> list(Authentication auth) {
        return repository.findByRecipientEmailOrderByCreatedAtDesc(auth.getName()).stream()
                .map(n -> new NotificationResponse(n.getId(), n.getMessage(),
                        n.getProjectId(), n.isRead(), n.getCreatedAt()))
                .toList();
    }

    @PatchMapping("/{id}/read")
    @Transactional
    public void markAsRead(@PathVariable Long id, Authentication auth) {
        Notification n = repository.findByIdAndRecipientEmail(id, auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        n.setRead(true);
    }
}