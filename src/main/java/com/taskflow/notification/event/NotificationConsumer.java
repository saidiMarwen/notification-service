package com.taskflow.notification.event;

import com.taskflow.notification.model.Notification;
import com.taskflow.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final ObjectMapper objectMapper;
    private final NotificationRepository repository;

    @KafkaListener(topics = "task-events")
    public void onTaskAssigned(String json) throws Exception {
        TaskAssignedEvent event = objectMapper.readValue(json, TaskAssignedEvent.class);
        log.info("Événement reçu : {}", event);

        repository.save(Notification.builder()
                .recipientEmail(event.assigneeEmail())
                .projectId(event.projectId())
                .message(event.assignedBy() + " t'a assigné la tâche « " + event.taskTitle() + " »")
                .build());
    }
}
