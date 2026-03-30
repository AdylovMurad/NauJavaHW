package ru.murad.NauJava.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.murad.NauJava.entity.User;
import ru.murad.NauJava.service.UserService;

@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model) {
        try {
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                user.setRoles("USER");
            }

            userService.saveUser(user);
            return "redirect:/login";
        } catch (Exception ex) {
            model.addAttribute("message", "Ошибка: Пользователь с таким логином уже существует");
            return "registration";
        }
    }
}