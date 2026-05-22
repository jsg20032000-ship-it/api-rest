package com.openwebinars.todo.service;

import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.repos.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public Category save(String title) {
        return categoryRepository.save(Category.builder().title(title).build());
    }

    public Category edit(Long id, String title) {
        Category category = findById(id);
        category.setTitle(title);
        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}