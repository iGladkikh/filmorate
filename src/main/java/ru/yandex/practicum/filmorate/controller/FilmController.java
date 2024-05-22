package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicateElementException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends Controller<Film> {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        log.debug(DEBUG_LOG_PATTERN, "create", film);
        try {
            if (films.containsValue(film)) {
                throw new DuplicateElementException("Такой фильм уже существует");
            }
            film.setId(getNextId(films));
            films.put(film.getId(), film);
            return film;
        } catch (Exception e) {
            log.error(ERROR_LOG_PATTERN, "create", film, e.getMessage(), e.getClass());
            throw e;
        }
    }

    @Override
    @PutMapping
    public Film update(@RequestBody @Valid Film newFilm) {
        log.debug(DEBUG_LOG_PATTERN, "update", newFilm);
        try {
            if (films.containsKey(newFilm.getId())) {
                if ((newFilm.getName() != null || newFilm.getReleaseDate() != null) && films.containsValue(newFilm)) {
                    throw new DuplicateElementException("Такой фильм уже существует");
                }

                Film oldFilm = films.get(newFilm.getId());

                if (newFilm.getName() != null) {
                    oldFilm.setName(newFilm.getName());
                }
                if (newFilm.getDescription() != null) {
                    oldFilm.setDescription(newFilm.getDescription());
                }
                if (newFilm.getReleaseDate() != null) {
                    oldFilm.setReleaseDate(newFilm.getReleaseDate());
                }
                if (newFilm.getDuration() != null) {
                    oldFilm.setDuration(newFilm.getDuration());
                }

                return oldFilm;
            }
            throw new NotFoundException("Фильм не найден");
        } catch (Exception e) {
            log.error(ERROR_LOG_PATTERN, "update", newFilm, e.getMessage(), e.getClass());
            throw e;
        }
    }
}
