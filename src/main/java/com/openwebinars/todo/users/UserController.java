package com.openwebinars.todo.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Registro de nuevo usuario")
    @PostMapping("/auth/register")
    public ResponseEntity<NewUserResponse> createUser(@RequestBody NewUserCommand cmd) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(NewUserResponse.of(userService.register(cmd)));
    }

    @Operation(summary = "Modificar perfil del usuario autenticado")
    @SecurityRequirement(name = "basicAuth")
    @PutMapping("/user/profile")
    public NewUserResponse editProfile(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> body) {
        return NewUserResponse.of(userService.edit(user.getId(), body));
    }
}