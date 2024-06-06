package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.logger.LoggerMessagePattern;

import java.util.*;

@Slf4j
@Getter
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(long id) {
        Optional<User> userOptional = userStorage.findById(id);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        return userOptional.get();
    }

    public User create(User user) {
        log.debug(LoggerMessagePattern.DEBUG, "create", user);
        try {
            if (findUserByIdEmail(user.getEmail()).isPresent()) {
                throw new DuplicatedDataException("Этот email уже используется");
            }

            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            return userStorage.create(user);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "create", user, e.getMessage(), e.getClass());
            throw e;
        }
    }

    public User update(User newUser) {
        log.debug(LoggerMessagePattern.DEBUG, "update", newUser);
        try {
            Optional<User> oldUserOptional = userStorage.findById(newUser.getId());
            if (oldUserOptional.isEmpty()) {
                throw new NotFoundException("Пользователь не найден");
            }

            fillEmptyFields(newUser, oldUserOptional.get());

            Optional<User> userWithSameEmail = findUserByIdEmail(newUser.getEmail());
            if (userWithSameEmail.isPresent() && !Objects.equals(userWithSameEmail.get().getId(), newUser.getId())) {
                throw new DuplicatedDataException("Этот email уже используется");
            }

            return userStorage.update(newUser);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "update", newUser, e.getMessage(), e.getClass());
            throw e;
        }
    }

    private static void fillEmptyFields(User target, User source) {
        if (target.getName() == null) {
            target.setName(source.getName());
        }
        if (target.getLogin() == null) {
            target.setLogin(source.getLogin());
        }
        if (target.getEmail() == null) {
            target.setEmail(source.getEmail());
        }
        if (target.getBirthday() == null) {
            target.setBirthday(source.getBirthday());
        }
        target.setFriends(source.getFriends());
    }

    private Optional<User> findUserByIdEmail(String email) {
        return userStorage.findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    public List<User> findFriends(long id) {
        log.debug(LoggerMessagePattern.DEBUG, "find friends", "userId=%d".formatted(id));
        try {
            Optional<User> userOptional = userStorage.findById(id);
            if (userOptional.isEmpty()) {
                throw new NotFoundException("Пользователь не найден");
            }

            Set<Long> friends = userOptional.get().getFriends();
            return findByIds(friends);
        } catch (Exception e) {
            log.warn(
                    LoggerMessagePattern.WARN,
                    "find friends",
                    "userId=%d".formatted(id),
                    e.getMessage(),
                    e.getClass()
            );
            throw e;
        }
    }

    public List<User> findCommonFriends(long id1, long id2) {
        log.debug(LoggerMessagePattern.DEBUG, "find common friends", "users %d and %d".formatted(id1, id2));
        try {
            Optional<User> userOptional1 = userStorage.findById(id1);
            Optional<User> userOptional2 = userStorage.findById(id2);
            if (userOptional1.isEmpty() || userOptional2.isEmpty()) {
                throw new NotFoundException("Пользователь не найден");
            }

            Set<Long> friends1 = userOptional1.get().getFriends();
            Set<Long> friends2 = userOptional2.get().getFriends();

            Set<Long> commonFriends = new HashSet<>(friends1);
            commonFriends.retainAll(friends2);
            return findByIds(commonFriends);
        } catch (Exception e) {
            log.warn(
                    LoggerMessagePattern.WARN,
                    "find common friends",
                    "users %d and %d".formatted(id1, id2),
                    e.getMessage(),
                    e.getClass()
            );
            throw e;
        }
    }

    public List<User> findByIds(Collection<Long> ids) {
        return userStorage.findAll().stream()
                .filter(user -> ids.contains(user.getId()))
                .toList();
    }

    public void delete(long id) {
        log.debug(LoggerMessagePattern.DEBUG, "delete", "id=%d".formatted(id));
        try {
            if (userStorage.findById(id).isEmpty()) {
                throw new NotFoundException("Пользователь не найден");
            }
            userStorage.delete(id);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "delete", "id=%d".formatted(id), e.getMessage(), e.getClass());
            throw e;
        }
    }

    public User addFriend(long userId, long friendId) {
        log.debug(LoggerMessagePattern.DEBUG, "add friend", "userId=%d, friendId=%d".formatted(userId, friendId));
        try {
            Optional<User> userOptional = userStorage.findById(userId);
            Optional<User> friendOptional = userStorage.findById(friendId);
            if (userOptional.isEmpty() || friendOptional.isEmpty()) {
                throw new NotFoundException("Пользователь не найден");
            }

            User user = userOptional.get();
            User friend = friendOptional.get();

            if (user.getFriends().contains(friendId)) {
                throw new DuplicatedDataException("Пользователь #%d ранее добавлен в список друзей".formatted(friendId));
            }

            user.getFriends().add(friendId);
            friend.getFriends().add(userId);

            return user;
        } catch (Exception e) {
            log.warn(
                    LoggerMessagePattern.WARN,
                    "add friend",
                    "userId=%d, friendId=%d".formatted(userId, friendId),
                    e.getMessage(),
                    e.getClass()
            );
            throw e;
        }
    }

    public User deleteFriend(long userId, long friendId) {
        log.debug(LoggerMessagePattern.DEBUG, "delete friend", "userId=%d, friendId=%d".formatted(userId, friendId));
        try {
            Optional<User> userOptional = userStorage.findById(userId);
            Optional<User> friendOptional = userStorage.findById(friendId);
            if (userOptional.isEmpty() || friendOptional.isEmpty()) {
                throw new NotFoundException("Пользователь не найден");
            }

            User user = userOptional.get();
            user.getFriends().remove(friendId);

            User friend = friendOptional.get();
            friend.getFriends().remove(userId);

            return user;
        } catch (Exception e) {
            log.warn(
                    LoggerMessagePattern.WARN,
                    "delete friend",
                    "userId=%d, friendId=%d".formatted(userId, friendId),
                    e.getMessage(),
                    e.getClass()
            );
            throw e;
        }
    }
}
