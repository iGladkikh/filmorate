package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    @Override
    public List<User> findByIds(Collection<Long> ids) {
        return users.values().stream()
                .filter(user -> ids.contains(user.getId()))
                .toList();
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
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

    @Override
    public Optional<User> findEqual(User user) {
            return users.values().stream()
                    .filter(u -> u.equals(user))
                    .findFirst();
    }

    @Override
    public User addFriend(long userId, long friendId) {
        User user = users.get(userId);
        user.getFriends().add(friendId);
        users.get(friendId).getFriends().add(userId);
        return user;
    }

    @Override
    public User deleteFriend(long userId, long friendId) {
        User user = users.get(userId);
        user.getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(userId);
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
