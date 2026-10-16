package com.taskflow.notification.event;

import com.taskflow.notification.model.Notification;
import com.taskflow.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Spy ObjectMapper objectMapper = new ObjectMapper();
    @Mock NotificationRepository repository;
    @InjectMocks NotificationConsumer consumer;

    @Test
    void createsNotificationForAssignee() throws Exception {
        String json = """
            {"taskId":5,"taskTitle":"Maquettes","projectId":1,
             "assigneeEmail":"bob@test.com","assignedBy":"marwen@test.com"}
            """;

        consumer.onTaskAssigned(json);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(repository).save(captor.capture());
        Notification n = captor.getValue();
        assertEquals("bob@test.com", n.getRecipientEmail());
        assertEquals(1L, n.getProjectId());
        assertTrue(n.getMessage().contains("Maquettes"));
        assertFalse(n.isRead());
    }

    @Test
    void invalidJsonThrowsAndSavesNothing() {
        assertThrows(Exception.class, () -> consumer.onTaskAssigned("pas du json"));
        verify(repository, never()).save(any());
    }
}