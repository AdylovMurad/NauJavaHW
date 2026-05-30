package ru.murad.SmartLibrary.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.murad.SmartLibrary.controller.dto.UserRequest;
import ru.murad.SmartLibrary.controller.dto.UserResponse;
import ru.murad.SmartLibrary.entity.User;
import ru.murad.SmartLibrary.exception.ResourceNotFoundException;
import ru.murad.SmartLibrary.service.UserService;
import ru.murad.SmartLibrary.repository.UserRepository;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * REST-контроллер для просмотра и администрирования пользователей через API.
 */
@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);

    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * Конструктор контроллера UserRestController.
     *
     * @param userRepository репозиторий пользователей
     * @param userService    сервис управления пользователями
     */
    public UserRestController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Возвращает список всех зарегистрированных пользователей.
     *
     * @return ResponseEntity со списком DTO-ответов пользователей
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        List<UserResponse> users = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return ResponseEntity со сведениями о пользователе
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        return ResponseEntity.ok(UserResponse.from(user));
    }

    /**
     * Создает нового пользователя через API.
     *
     * @param request DTO с параметрами нового пользователя
     * @return ResponseEntity с созданным пользователем
     */
    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        } else {
            user.setRole(ru.murad.SmartLibrary.entity.UserRole.ROLE_USER);
        }
        userService.saveUser(user);
        logger.info("REST created user id={} username='{}' role={}", user.getId(), user.getUsername(), user.getRole());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    /**
     * Обновляет профиль пользователя по его идентификатору.
     *
     * @param id      идентификатор пользователя
     * @param request DTO с измененными параметрами пользователя
     * @return ResponseEntity с обновленным профилем
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @RequestBody UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(request.getPassword());
            userService.saveUser(user);
        } else {
            userRepository.save(user);
        }

        logger.info("REST updated user id={} username='{}' role={}", user.getId(), user.getUsername(), user.getRole());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    /**
     * Удаляет пользователя из системы.
     *
     * @param id идентификатор пользователя
     * @return ResponseEntity без тела (no content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(id);
        logger.info("REST deleted user id={}", id);
        return ResponseEntity.noContent().build();
    }
}
