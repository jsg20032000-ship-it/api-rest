package com.openwebinars.todo.dto;

import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.users.NewUserResponse;

import java.time.LocalDateTime;

public record GetTaskDto(
        Long id,
        String title,
        String description,
        LocalDateTime createdAt,
        LocalDateTime deadline,
        NewUserResponse author){

    public static GetTaskDto of(Task t) {
        return new GetTaskDto(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getCreatedAt(),
                t.getDeadline(),
                NewUserResponse.of(t.getAuthor())
        );
    }

}
