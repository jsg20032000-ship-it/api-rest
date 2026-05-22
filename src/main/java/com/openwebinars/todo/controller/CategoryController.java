package com.openwebinars.todo.controller;

import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Listar categorías disponibles")
    @GetMapping
    public List<Category> getAll() {
        return categoryService.findAll();
    }
}