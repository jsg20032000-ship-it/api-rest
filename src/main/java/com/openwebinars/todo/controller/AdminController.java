package com.openwebinars.todo.controller;

import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.service.CategoryService;
import com.openwebinars.todo.users.NewUserResponse;
import com.openwebinars.todo.users.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final CategoryService categoryService;

    // --- Usuarios ---

    @Operation(summary = "Listar todos los usuarios")
    @GetMapping("/users")
    public List<NewUserResponse> getAllUsers() {
        return userService.findAll().stream()
                .map(NewUserResponse::of)
                .toList();
    }

    @Operation(summary = "Ver un usuario concreto")
    @GetMapping("/users/{id}")
    public NewUserResponse getUserById(@PathVariable Long id) {
        return NewUserResponse.of(userService.findById(id));
    }

    @Operation(summary = "Editar un usuario")
    @PutMapping("/users/{id}")
    public NewUserResponse editUser(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return NewUserResponse.of(userService.edit(id, body));
    }

    @Operation(summary = "Eliminar un usuario")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Promover usuario a GESTOR")
    @PostMapping("/users/{id}/promote")
    public NewUserResponse promote(@PathVariable Long id) {
        return NewUserResponse.of(userService.promote(id));
    }

    @Operation(summary = "Degradar GESTOR a USER")
    @PostMapping("/users/{id}/demote")
    public NewUserResponse demote(@PathVariable Long id) {
        return NewUserResponse.of(userService.demote(id));
    }

    // --- Categorías ---

    @Operation(summary = "Listar todas las categorías")
    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryService.findAll();
    }

    @Operation(summary = "Crear categoría")
    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.save(body.get("title")));
    }

    @Operation(summary = "Editar categoría")
    @PutMapping("/categories/{id}")
    public Category editCategory(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return categoryService.edit(id, body.get("title"));
    }

    @Operation(summary = "Eliminar categoría")
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}