package com.openwebinars.todo.users;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(NewUserCommand cmd) {
        User user = User.builder()
                .username(cmd.username())
                .email(cmd.email())
                .password(passwordEncoder.encode(cmd.password()))
                .fullname(cmd.fullname())
                .role(UserRole.USER)
                .build();
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User edit(Long id, Map<String, String> body) {
        User user = findById(id);
        if (body.containsKey("username")) user.setUsername(body.get("username"));
        if (body.containsKey("email")) user.setEmail(body.get("email"));
        if (body.containsKey("fullname")) user.setFullname(body.get("fullname"));
        if (body.containsKey("password")) user.setPassword(passwordEncoder.encode(body.get("password")));
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public User promote(Long id) {
        User user = findById(id);
        user.setRole(UserRole.GESTOR);
        return userRepository.save(user);
    }

    public User demote(Long id) {
        User user = findById(id);
        user.setRole(UserRole.USER);
        return userRepository.save(user);
    }
}