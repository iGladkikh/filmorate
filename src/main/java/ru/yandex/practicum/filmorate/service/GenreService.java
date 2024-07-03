package ru.yandex.practicum.filmorate.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GenreService extends BaseService<Genre> {

    @Autowired
    protected GenreService(GenreStorage storage) {
        super(storage);
    }

    @PostConstruct
    void addData() {
        List<Genre> genres = new ArrayList<>();
        genres.add(Genre.builder().name("Комедия").build());
        genres.add(Genre.builder().name("Драма").build());
        genres.add(Genre.builder().name("Мультфильм").build());
        genres.add(Genre.builder().name("Триллер").build());
        genres.add(Genre.builder().name("Документальный").build());
        genres.add(Genre.builder().name("Боевик").build());

        for (Genre genre : genres) {
            try {
                create(genre);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
    }
}
