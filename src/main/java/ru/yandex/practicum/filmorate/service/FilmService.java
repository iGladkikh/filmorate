package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.logger.LoggerMessagePattern;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Getter
@Service
public class FilmService extends BaseService<Film> {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, GenreStorage genreStorage, RatingStorage ratingStorage) {
        super(filmStorage);
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.ratingStorage = ratingStorage;
    }

    public List<Film> findPopular(Optional<Integer> count) {
        final Comparator<Film> likesCountComparator = Comparator.comparing(Film::getLikesCount).reversed();
        final int defaultCount = 10;
        return filmStorage.findPopular(likesCountComparator, count.orElse(defaultCount));
    }

    @Override
    public Film create(Film film) {
        log.debug(LoggerMessagePattern.DEBUG, "create", film);
        try {
            if (film.getGenres() == null) {
                film.setGenres(new LinkedHashSet<>());
            } else {
                for (Genre genre : film.getGenres()) {
                    if (genreStorage.findById(genre.getId()).isEmpty()) {
                        throw new ValidationException("Жанр фильма не найден");
                    }
                }
            }

            if (film.getMpa() != null && ratingStorage.findById(film.getMpa().getId()).isEmpty()) {
                throw new ValidationException("Рейтинг фильма не найден");
            }

            if (film.getLikes() == null) {
                film.setLikes(new LinkedHashSet<>());
            }

            return filmStorage.create(film);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "create", film, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    public Film update(Film film) {
        log.debug(LoggerMessagePattern.DEBUG, "update", film);
        try {
            Optional<Film> oldFilmOpt = super.findById(film.getId());
            if (oldFilmOpt.isEmpty()) {
                throw new NotFoundException("Фильм не найден");
            }

            if (film.getGenres() != null) {
                for (Genre genre : film.getGenres()) {
                    Optional<Genre> genreOptional = genreStorage.findById(genre.getId());
                    if (genreOptional.isEmpty()) {
                        throw new ValidationException("Жанр фильма не найден");
                    }
                }
            }

            if (film.getMpa() != null && ratingStorage.findById(film.getMpa().getId()).isEmpty()) {
                throw new ValidationException("Рейтинг фильма не найден");
            }

            fillEmptyFields(film, oldFilmOpt.get());

            return filmStorage.update(film);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "update", film, e.getMessage(), e.getClass());
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
        if (target.getGenres() == null) {
            target.setGenres(source.getGenres());
        }
        if (target.getMpa() == null) {
            target.setMpa(source.getMpa());
        }
        target.setLikes(source.getLikes());
    }

    public Film addLike(long filmId, long userId) {
        log.debug(LoggerMessagePattern.DEBUG, "add like", "filmId=%d, userId=%d".formatted(filmId, userId));
        try {
            Optional<Film> filmOptional = findById(filmId);
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
            return filmStorage.addLike(filmId, userId);
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
            Optional<Film> filmOptional = findById(filmId);
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
            return filmStorage.deleteLike(filmId, userId);
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
}
