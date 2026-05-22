package com.openwebinars.todo.users;

public record NewUserResponse(
        Long id,
        String username,
        String email,
        String fullname
) {
    public static NewUserResponse of(User u) {
        return new NewUserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getFullname());
    }
}