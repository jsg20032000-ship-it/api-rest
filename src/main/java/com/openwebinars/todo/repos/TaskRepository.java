package com.openwebinars.todo.repos;

import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAuthor(User author);

}
