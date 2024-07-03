package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> findAll() {
        return List.copyOf(films.values());
    }

    @Override
    public Optional<Film> findById(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(long id) {
        films.remove(id);
    }

    @Override
    public Optional<Film> findEqual(Film film) {
        return films.values().stream()
                .filter(f -> f.equals(film))
                .findFirst();
    }

    @Override
    public List<Film> findPopular(Comparator<Film> comparator, int count) {
        return films.values().stream()
                .sorted(comparator)
                .limit(count)
                .toList();
    }

    @Override
    public Film addLike(long filmId, long userId) {
        Film film = films.get(filmId);
        film.getLikes().add(userId);
        return film;
    }

    @Override
    public Film deleteLike(long filmId, long userId) {
        Film film = films.get(filmId);
        film.getLikes().remove(userId);
        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
