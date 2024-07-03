package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Comparator;
import java.util.List;

@Qualifier("film")
public interface FilmStorage extends Storage<Film> {

    List<Film> findPopular(Comparator<Film> comparator, int count);

    Film addLike(long filmId, long userId);

    Film deleteLike(long filmId, long userId);
}
