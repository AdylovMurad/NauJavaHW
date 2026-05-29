package ru.murad.NauJava.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.murad.NauJava.entity.User;
import ru.murad.NauJava.entity.UserRole;
import ru.murad.NauJava.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("adminpass"));
            admin.setEmail("admin@library.ru");
            admin.setFirstName("Главный");
            admin.setLastName("Администратор");
            admin.setRole(UserRole.ROLE_ADMIN);

            userRepository.save(admin);
            System.out.println(">>> [INIT] Администратор успешно создан! Логин: admin, Пароль: adminpass");
        }
    }
}