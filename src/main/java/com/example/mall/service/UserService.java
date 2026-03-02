package com.example.mall.service;

import com.example.mall.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    private final AtomicLong userIdGenerator = new AtomicLong(1);
    private final Map<String, User> usersByUsername = new ConcurrentHashMap<>();

    public User register(String username, String password) {
        if (usersByUsername.containsKey(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        User user = new User(userIdGenerator.getAndIncrement(), username, password);
        usersByUsername.put(username, user);
        return user;
    }

    public User login(String username, String password) {
        User user = usersByUsername.get(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        return user;
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }
}
