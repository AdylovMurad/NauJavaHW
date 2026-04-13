package ru.murad.NauJava;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.murad.NauJava.entity.User;
import ru.murad.NauJava.exception.ResourceNotFoundException;
import ru.murad.NauJava.repository.UserRepository;
import ru.murad.NauJava.service.UserServiceImpl;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void saveUser_Success() {
        User user = new User();
        user.setUsername("TestUser");
        user.setPassword("12345");

        Mockito.when(passwordEncoder.encode("12345")).thenReturn("encoded_hash");

        userService.saveUser(user);

        Assertions.assertEquals("encoded_hash", user.getPassword());
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    public void findByUsername_Success() {
        User user = new User();
        user.setUsername("TestUser");
        Mockito.when(userRepository.findByUsername("TestUser")).thenReturn(Optional.of(user));

        User found = userService.findByUsername("TestUser");

        Assertions.assertNotNull(found);
        Assertions.assertEquals("TestUser", found.getUsername());
    }

    @Test
    public void findByUsername_NotFound_ThrowsException() {
        Mockito.when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            userService.findByUsername("unknown");
        });
    }
}