package com.openwebinars.todo.repos;

import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.model.Priority;
import com.openwebinars.todo.model.Tag;
import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAuthor(User author);

    List<Task> findByAuthorAndTitleContainingIgnoreCase(User author, String title);

    List<Task> findByAuthorAndCompleted(User author, boolean completed);

    List<Task> findByAuthorAndCategory(User author, Category category);

    List<Task> findByAuthorAndPriority(User author, Priority priority);

    List<Task> findByAuthorAndDeadlineBefore(User author, LocalDateTime deadline);

    List<Task> findByAuthorAndTagsContaining(User author, Tag tag);
}