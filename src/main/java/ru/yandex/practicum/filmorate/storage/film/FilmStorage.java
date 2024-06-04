package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAll();

    Optional<Film> findById(long id);

    List<Film> findPopular(Comparator<Film> comparator, int count);

    Film create(Film film);

    Film update(Film newFilm);

    void delete(long id);
}
