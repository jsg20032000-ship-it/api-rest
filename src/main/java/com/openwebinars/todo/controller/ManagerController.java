package com.openwebinars.todo.controller;

import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.service.CategoryService;
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
@RequestMapping("/manager")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
@PreAuthorize("hasRole('GESTOR') or hasRole('ADMIN')")
public class ManagerController {

    private final CategoryService categoryService;

    @Operation(summary = "Listar categorías")
    @GetMapping("/categories")
    public List<Category> getAll() {
        return categoryService.findAll();
    }

    @Operation(summary = "Crear categoría")
    @PostMapping("/categories")
    public ResponseEntity<Category> create(@RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.save(body.get("title")));
    }

    @Operation(summary = "Editar categoría")
    @PutMapping("/categories/{id}")
    public Category edit(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return categoryService.edit(id, body.get("title"));
    }

    @Operation(summary = "Eliminar categoría")
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}