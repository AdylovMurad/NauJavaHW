package ru.murad.SmartLibrary.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.murad.SmartLibrary.entity.User;
import ru.murad.SmartLibrary.repository.UserRepository;
import ru.murad.SmartLibrary.exception.ResourceNotFoundException;

/**
 * Реализация сервиса управления пользователями.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param userRepository  репозиторий пользователей
     * @param passwordEncoder кодировщик паролей
     */
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
