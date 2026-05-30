package ru.murad.NauJava.service;

import ru.murad.NauJava.entity.User;

/**
 * Сервис для управления пользователями системы.
 */
public interface UserService {
    /**
     * Выполняет поиск пользователя по его уникальному логину.
     *
     * @param username уникальное имя пользователя
     * @return найденный пользователь
     * @throws ru.murad.NauJava.exception.ResourceNotFoundException если пользователь не найден
     */
    User findByUsername(String username);

    /**
     * Хеширует пароль и сохраняет пользователя в базе данных.
     *
     * @param user сущность пользователя для сохранения
     */
    void saveUser(User user);
}
