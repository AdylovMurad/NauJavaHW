package ru.murad.NauJava.service;

import ru.murad.NauJava.entity.User;

public interface UserService {
    User findByUsername(String username);
    void saveUser(User user);
}