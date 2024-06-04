package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable @Positive long id) {
        return userService.findById(id);
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        return userService.update(newUser);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive long id) {
        userService.delete(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> findFriends(@PathVariable @Positive long id) {
        return userService.findFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable @Positive long id, @PathVariable @Positive long otherId) {
        return userService.findCommonFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable @Positive long id, @PathVariable @Positive long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable @Positive long id, @PathVariable @Positive long friendId) {
        return userService.deleteFriend(id, friendId);
    }
}
