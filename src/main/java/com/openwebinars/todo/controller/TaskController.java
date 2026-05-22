package com.openwebinars.todo.controller;

import com.openwebinars.todo.dto.EditTaskDto;
import com.openwebinars.todo.dto.GetTaskDto;
import com.openwebinars.todo.model.Priority;
import com.openwebinars.todo.service.TaskService;
import com.openwebinars.todo.users.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Obtener todas las tareas del usuario")
    @GetMapping
    public List<GetTaskDto> getAll(@AuthenticationPrincipal User author) {
        return taskService.findByAuthor(author)
                .stream()
                .map(GetTaskDto::of)
                .toList();
    }

    @Operation(summary = "Obtener una tarea por ID")
    @PostAuthorize("returnObject.author().username() == authentication.principal.username")
    @GetMapping("/{id}")
    public GetTaskDto getById(@PathVariable Long id) {
        return GetTaskDto.of(taskService.findById(id));
    }

    @Operation(summary = "Crear una tarea")
    @PostMapping
    public ResponseEntity<GetTaskDto> create(
            @RequestBody EditTaskDto cmd,
            @AuthenticationPrincipal User author) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GetTaskDto.of(taskService.save(cmd, author)));
    }

    @Operation(summary = "Editar una tarea")
    @PreAuthorize("@ownerCheck.check(#id, authentication.principal.getId())")
    @PutMapping("/{id}")
    public GetTaskDto edit(@RequestBody EditTaskDto cmd, @PathVariable Long id) {
        return GetTaskDto.of(taskService.edit(cmd, id));
    }

    @Operation(summary = "Eliminar una tarea")
    @PreAuthorize("@ownerCheck.check(#id, authentication.principal.getId())")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar tareas por distintos criterios")
    @GetMapping("/search")
    public List<GetTaskDto> search(
            @AuthenticationPrincipal User author,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deadlineBefore) {

        if (title != null)
            return taskService.findByAuthorAndTitle(author, title).stream().map(GetTaskDto::of).toList();
        if (completed != null)
            return taskService.findByAuthorAndCompleted(author, completed).stream().map(GetTaskDto::of).toList();
        if (category != null)
            return taskService.findByAuthorAndCategory(author, category).stream().map(GetTaskDto::of).toList();
        if (priority != null)
            return taskService.findByAuthorAndPriority(author, priority).stream().map(GetTaskDto::of).toList();
        if (deadlineBefore != null)
            return taskService.findByAuthorAndDeadlineBefore(author, deadlineBefore).stream().map(GetTaskDto::of).toList();

        return taskService.findByAuthor(author).stream().map(GetTaskDto::of).toList();
    }

    @Operation(summary = "Añadir un tag a una tarea")
    @PreAuthorize("@ownerCheck.check(#id, authentication.principal.getId())")
    @PostMapping("/{id}/tags")
    public GetTaskDto addTag(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        return GetTaskDto.of(taskService.addTag(id, body.get("tagId")));
    }

    @Operation(summary = "Eliminar un tag de una tarea")
    @PreAuthorize("@ownerCheck.check(#id, authentication.principal.getId())")
    @DeleteMapping("/{id}/tags/{tagId}")
    public GetTaskDto removeTag(@PathVariable Long id, @PathVariable Long tagId) {
        return GetTaskDto.of(taskService.removeTag(id, tagId));
    }

    @Operation(summary = "Buscar tareas por tag")
    @GetMapping("/by-tag")
    public List<GetTaskDto> getByTag(
            @AuthenticationPrincipal User author,
            @RequestParam Long tagId) {
        return taskService.findByAuthorAndTag(author, tagId).stream().map(GetTaskDto::of).toList();
    }

    @Operation(summary = "Dashboard con estadísticas de tareas")
    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(@AuthenticationPrincipal User author) {
        List<GetTaskDto> tasks = taskService.findByAuthor(author).stream().map(GetTaskDto::of).toList();
        long total = tasks.size();
        long completed = tasks.stream().filter(GetTaskDto::completed).count();
        long pending = total - completed;
        long overdue = tasks.stream()
                .filter(t -> t.deadline() != null && t.deadline().isBefore(LocalDateTime.now()) && !t.completed())
                .count();
        return ResponseEntity.ok(Map.of(
                "total", total,
                "completed", completed,
                "pending", pending,
                "overdue", overdue
        ));
    }
}