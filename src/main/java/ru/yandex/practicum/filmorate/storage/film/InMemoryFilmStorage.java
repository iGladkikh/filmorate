package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Getter
@Component
@Primary
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
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
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
    public List<Film> findPopular(Comparator<Film> comparator, int count) {
        return films.values().stream()
                .sorted(comparator)
                .limit(count)
                .toList();
    }

    private long getNextId() {
        long currentMaxId = films.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
