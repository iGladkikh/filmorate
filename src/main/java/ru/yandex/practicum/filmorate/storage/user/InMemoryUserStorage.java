package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Getter
@Component
@Primary
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    private long getNextId() {
        long currentMaxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
