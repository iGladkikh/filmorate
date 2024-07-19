package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Collection;
import java.util.List;

public interface UserStorage extends Storage<User> {

    List<User> findByIds(Collection<Long> ids);

    User addFriend(long userId, long friendId);

    User deleteFriend(long userId, long friendId);
}
