package ru.murad.SmartLibrary.repository;

import ru.murad.SmartLibrary.entity.User;
import java.util.Optional;

public interface UserRepository extends org.springframework.data.repository.CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}