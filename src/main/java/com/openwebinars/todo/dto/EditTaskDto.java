package com.openwebinars.todo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.openwebinars.todo.model.Priority;

import java.time.LocalDateTime;

public record EditTaskDto(
        String title,
        String description,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime deadline,
        boolean completed,
        Priority priority,
        Long categoryId
) {}