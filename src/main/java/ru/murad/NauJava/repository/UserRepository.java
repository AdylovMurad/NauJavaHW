package ru.murad.NauJava.repository;

import ru.murad.NauJava.entity.User;
import java.util.Optional;

public interface UserRepository extends org.springframework.data.repository.CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}