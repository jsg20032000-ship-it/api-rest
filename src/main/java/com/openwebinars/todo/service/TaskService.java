package com.openwebinars.todo.service;

import com.openwebinars.todo.dto.EditTaskDto;
import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.model.Priority;
import com.openwebinars.todo.model.Tag;
import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.repos.CategoryRepository;
import com.openwebinars.todo.repos.TagRepository;
import com.openwebinars.todo.repos.TaskRepository;
import com.openwebinars.todo.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public List<Task> findByAuthor(User author) {
        return taskRepository.findByAuthor(author);
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public Task save(EditTaskDto cmd, User author) {
        Category category = null;
        if (cmd.categoryId() != null) {
            category = categoryRepository.findById(cmd.categoryId()).orElse(null);
        }
        Task task = Task.builder()
                .title(cmd.title())
                .description(cmd.description())
                .deadline(cmd.deadline())
                .completed(cmd.completed())
                .priority(cmd.priority() != null ? cmd.priority() : com.openwebinars.todo.model.Priority.MEDIUM)
                .category(category)
                .author(author)
                .build();
        return taskRepository.save(task);
    }

    public Task edit(EditTaskDto cmd, Long id) {
        Task task = findById(id);
        task.setTitle(cmd.title());
        task.setDescription(cmd.description());
        task.setDeadline(cmd.deadline());
        task.setCompleted(cmd.completed());
        if (cmd.priority() != null) task.setPriority(cmd.priority());
        if (cmd.categoryId() != null) {
            categoryRepository.findById(cmd.categoryId()).ifPresent(task::setCategory);
        }
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    // Búsquedas
    public List<Task> findByAuthorAndTitle(User author, String title) {
        return taskRepository.findByAuthorAndTitleContainingIgnoreCase(author, title);
    }

    public List<Task> findByAuthorAndCompleted(User author, boolean completed) {
        return taskRepository.findByAuthorAndCompleted(author, completed);
    }

    public List<Task> findByAuthorAndCategory(User author, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return taskRepository.findByAuthorAndCategory(author, category);
    }

    public List<Task> findByAuthorAndPriority(User author, Priority priority) {
        return taskRepository.findByAuthorAndPriority(author, priority);
    }

    public List<Task> findByAuthorAndDeadlineBefore(User author, LocalDateTime deadline) {
        return taskRepository.findByAuthorAndDeadlineBefore(author, deadline);
    }

    public List<Task> findByAuthorAndTag(User author, Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        return taskRepository.findByAuthorAndTagsContaining(author, tag);
    }

    // Gestión de tags en tareas
    public Task addTag(Long taskId, Long tagId) {
        Task task = findById(taskId);
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        task.getTags().add(tag);
        return taskRepository.save(task);
    }

    public Task removeTag(Long taskId, Long tagId) {
        Task task = findById(taskId);
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        task.getTags().remove(tag);
        return taskRepository.save(task);
    }
}