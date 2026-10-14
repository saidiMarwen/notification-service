package com.taskflow.notification.event;

public record TaskAssignedEvent(Long taskId, String taskTitle, Long projectId,
                                String assigneeEmail, String assignedBy) {}