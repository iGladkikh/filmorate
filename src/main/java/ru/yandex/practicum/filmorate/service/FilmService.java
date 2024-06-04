package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.logger.LoggerMessagePattern;

import java.util.*;

@Slf4j
@Getter
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(long id) {
        Optional<Film> filmOptionalional = filmStorage.findById(id);
        if (filmOptionalional.isEmpty()) {
            throw new NotFoundException("Фильм не найден");
        }
        return filmOptionalional.get();
    }

    public Film create(Film film) {
        log.debug(LoggerMessagePattern.DEBUG, "create", film);
        try {
            if (findEqualFilm(film).isPresent()) {
                throw new DuplicatedDataException("Такой фильм уже существует");
            }
            return filmStorage.create(film);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "create", film, e.getMessage(), e.getClass());
            throw e;
        }
    }

    private Optional<Film> findEqualFilm(Film film) {
        return filmStorage.findAll().stream()
                .filter(f -> f.equals(film))
                .findFirst();
    }

    public Film update(Film newFilm) {
        log.debug(LoggerMessagePattern.DEBUG, "update", newFilm);
        try {
            Optional<Film> oldFilmOpt = filmStorage.findById(newFilm.getId());
            if (oldFilmOpt.isEmpty()) {
                throw new NotFoundException("Фильм не найден");
            }

            fillEmptyFields(newFilm, oldFilmOpt.get());

            Optional<Film> equalFilm = findEqualFilm(newFilm);
            if (equalFilm.isPresent() && !Objects.equals(equalFilm.get().getId(), newFilm.getId())) {
                throw new DuplicatedDataException("Такой фильм уже существует");
            }
            return filmStorage.update(newFilm);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "update", newFilm, e.getMessage(), e.getClass());
            throw e;
        }
    }

    private static void fillEmptyFields(Film target, Film source) {
        if (target.getName() == null) {
            target.setName(source.getName());
        }
        if (target.getDescription() == null) {
            target.setDescription(source.getDescription());
        }
        if (target.getReleaseDate() == null) {
            target.setReleaseDate(source.getReleaseDate());
        }
        if (target.getDuration() == null) {
            target.setDuration(source.getDuration());
        }
        target.setLikes(source.getLikes());
    }

    public void delete(long id) {
        log.debug(LoggerMessagePattern.DEBUG, "delete", "id=%d".formatted(id));
        try {
            if (filmStorage.findById(id).isEmpty()) {
                throw new NotFoundException("Фильм не найден");
            }
            filmStorage.delete(id);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "delete", "id=%d".formatted(id), e.getMessage(), e.getClass());
            throw e;
        }
    }

    public Film addLike(long filmId, long userId) {
        log.debug(LoggerMessagePattern.DEBUG, "add like", "filmId=%d, userId=%d".formatted(filmId, userId));
        try {
            Optional<Film> filmOptional = filmStorage.findById(filmId);
            if (filmOptional.isEmpty()) {
                throw new NotFoundException("Фильм не найден");
            }

            if (userStorage.findById(userId).isEmpty()) {
                throw new NotFoundException("Пользователь не найден");
            }

            Film film = filmOptional.get();
            if (film.getLikes().contains(userId)) {
                throw new DuplicatedDataException("Лайк от пользователя #%d добавлен ранее".formatted(userId));
            }
            film.getLikes().add(userId);
            return film;
        } catch (Exception e) {
            log.warn(
                    LoggerMessagePattern.WARN,
                    "add like",
                    "filmId=%d, userId=%d".formatted(filmId, userId),
                    e.getMessage(),
                    e.getClass()
            );
            throw e;
        }
    }

    public Film deleteLike(long filmId, long userId) {
        log.debug(LoggerMessagePattern.DEBUG, "delete like", "filmId=%d, userId=%d".formatted(filmId, userId));
        try {
            Optional<Film> filmOptional = filmStorage.findById(filmId);
            if (filmOptional.isEmpty()) {
                throw new NotFoundException("Фильм не найден");
            }

            if (userStorage.findById(userId).isEmpty()) {
                throw new NotFoundException("Пользователь не найден");
            }

            Film film = filmOptional.get();
            if (!film.getLikes().contains(userId)) {
                throw new NotFoundException("Лайк от пользователя #%d не найден".formatted(userId));
            }
            film.getLikes().remove(userId);
            return film;
        } catch (Exception e) {
            log.warn(
                    LoggerMessagePattern.WARN,
                    "delete like",
                    "filmId=%d, userId=%d".formatted(filmId, userId),
                    e.getMessage(),
                    e.getClass()
            );
            throw e;
        }
    }

    public List<Film> findPopular(Optional<Integer> count) {
        Comparator<Film> comparator = Comparator.comparing(Film::getLikesCount).reversed();
        int defaultCount = 10;
        return filmStorage.findPopular(comparator, count.orElse(defaultCount));
    }
}
