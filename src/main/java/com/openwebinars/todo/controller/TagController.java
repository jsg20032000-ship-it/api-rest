package com.openwebinars.todo.controller;

import com.openwebinars.todo.model.Tag;
import com.openwebinars.todo.service.TagService;
import com.openwebinars.todo.users.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class TagController {

    private final TagService tagService;

    @Operation(summary = "Listar tags del usuario")
    @GetMapping
    public List<Tag> getAll(@AuthenticationPrincipal User author) {
        return tagService.findByAuthor(author);
    }

    @Operation(summary = "Crear un tag")
    @PostMapping
    public ResponseEntity<Tag> create(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User author) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tagService.save(body.get("name"), author));
    }

    @Operation(summary = "Editar un tag")
    @PutMapping("/{id}")
    public Tag edit(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return tagService.edit(id, body.get("name"));
    }

    @Operation(summary = "Eliminar un tag")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}