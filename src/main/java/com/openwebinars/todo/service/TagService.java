package com.openwebinars.todo.service;

import com.openwebinars.todo.model.Tag;
import com.openwebinars.todo.repos.TagRepository;
import com.openwebinars.todo.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<Tag> findByAuthor(User author) {
        return tagRepository.findByAuthor(author);
    }

    public Tag findById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
    }

    public Tag save(String name, User author) {
        return tagRepository.save(Tag.builder().name(name).author(author).build());
    }

    public Tag edit(Long id, String name) {
        Tag tag = findById(id);
        tag.setName(name);
        return tagRepository.save(tag);
    }

    public void delete(Long id) {
        tagRepository.deleteById(id);
    }
}