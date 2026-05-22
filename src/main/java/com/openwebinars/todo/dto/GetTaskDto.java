package com.openwebinars.todo.dto;

import com.openwebinars.todo.model.Priority;
import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.users.NewUserResponse;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record GetTaskDto(
        Long id,
        String title,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deadline,
        boolean completed,
        Priority priority,
        String category,
        Set<String> tags,
        NewUserResponse author
) {
    public static GetTaskDto of(Task t) {
        return new GetTaskDto(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getCreatedAt(),
                t.getUpdatedAt(),
                t.getDeadline(),
                t.isCompleted(),
                t.getPriority(),
                t.getCategory() != null ? t.getCategory().getTitle() : null,
                t.getTags().stream().map(tag -> tag.getName()).collect(Collectors.toSet()),
                NewUserResponse.of(t.getAuthor())
        );
    }
}