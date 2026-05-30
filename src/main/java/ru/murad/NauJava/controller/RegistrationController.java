package ru.murad.NauJava.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.murad.NauJava.entity.User;
import ru.murad.NauJava.entity.UserRole;
import ru.murad.NauJava.service.UserService;

/**
 * Контроллер для регистрации новых читателей в системе.
 */
@Controller
public class RegistrationController {

    private final UserService userService;

    /**
     * Конструктор контроллера RegistrationController.
     *
     * @param userService сервис управления пользователями
     */
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Отображает страницу регистрации нового пользователя.
     *
     * @return имя Thymeleaf-шаблона "registration"
     */
    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    /**
     * Обрабатывает форму отправки данных нового пользователя.
     *
     * @param user  сущность регистрируемого пользователя
     * @param model объект модели Thymeleaf
     * @return перенаправление на страницу входа (/login) в случае успеха, либо возврат на страницу регистрации с ошибкой
     */
    @PostMapping("/registration")
    public String addUser(User user, Model model) {
        try {
            if (user.getRole() == null) {
                user.setRole(UserRole.ROLE_USER);
            }

            userService.saveUser(user);
            return "redirect:/login";
        } catch (Exception ex) {
            model.addAttribute("message", "Ошибка: Пользователь с таким логином или email уже существует");
            return "registration";
        }
    }
}
