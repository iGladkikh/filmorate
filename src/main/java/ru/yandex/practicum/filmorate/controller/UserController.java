package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicateElementException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends Controller<User> {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.debug(DEBUG_LOG_PATTERN, "create", user);
        try {
            if (isEmailAlreadyUsed(user.getEmail())) {
                throw new DuplicateElementException("Этот email уже используется");
            }

            user.setId(getNextId(users));
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            return user;
        } catch (Exception e) {
            log.error(ERROR_LOG_PATTERN, "create", user, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        log.debug(DEBUG_LOG_PATTERN, "update", newUser);
        try {
            if (users.containsKey(newUser.getId())) {
                User oldUser = users.get(newUser.getId());
                if (!oldUser.getEmail().equals(newUser.getEmail()) && isEmailAlreadyUsed(newUser.getEmail())) {
                    throw new DuplicateElementException("Этот email уже используется");
                }

                if (newUser.getName() != null) {
                    oldUser.setName(newUser.getName());
                }
                if (newUser.getLogin() != null) {
                    oldUser.setLogin(newUser.getLogin());
                }
                if (newUser.getEmail() != null) {
                    oldUser.setEmail(newUser.getEmail());
                }
                if (newUser.getBirthday() != null) {
                    oldUser.setBirthday(newUser.getBirthday());
                }
                return oldUser;
            }
            throw new NotFoundException("Пользователь не найден");
        } catch (Exception e) {
            log.error(ERROR_LOG_PATTERN, "update", newUser, e.getMessage(), e.getClass());
            throw e;
        }
    }

    private boolean isEmailAlreadyUsed(String email) {
        if (users.isEmpty()) return false;
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}
