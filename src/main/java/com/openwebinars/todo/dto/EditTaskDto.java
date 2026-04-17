package com.openwebinars.todo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record EditTaskDto(
        String title,
        String description,
        @JsonFormat(pattern = "yyy-MM-dd'T'HH:mm:ss")
        LocalDateTime deadline
) {
}
