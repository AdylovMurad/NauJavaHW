package ru.murad.SmartLibrary.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.murad.SmartLibrary.controller.dto.UserResponse;
import ru.murad.SmartLibrary.entity.User;
import ru.murad.SmartLibrary.exception.ResourceNotFoundException;
import ru.murad.SmartLibrary.repository.UserRepository;

/**
 * REST-контроллер для просмотра личного профиля текущего авторизованного пользователя.
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileRestController {

    private final UserRepository userRepository;

    /**
     * Конструктор контроллера ProfileRestController.
     *
     * @param userRepository репозиторий пользователей
     */
    public ProfileRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Возвращает профиль текущего авторизованного пользователя.
     *
     * @param userDetails данные текущего сеанса пользователя
     * @return ResponseEntity со сведениями о текущем пользователе
     */
    @GetMapping
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        return ResponseEntity.ok(UserResponse.from(user));
    }
}
