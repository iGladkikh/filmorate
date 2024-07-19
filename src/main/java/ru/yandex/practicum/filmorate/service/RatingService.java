package ru.yandex.practicum.filmorate.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RatingService extends BaseService<Rating> {

    @Autowired
    protected RatingService(RatingStorage storage) {
        super(storage);
    }

    @PostConstruct
    void addData() {
        List<Rating> ratings = new ArrayList<>();
        ratings.add(Rating.builder()
                .name("G")
                .description("Нет возрастных ограничений")
                .build()
        );
        ratings.add(Rating.builder()
                .name("PG")
                .description("Детям рекомендован просмотр родителями")
                .build()
        );
        ratings.add(Rating.builder()
                .name("PG-13")
                .description("Детям до 13 лет просмотр не желателен")
                .build()
        );
        ratings.add(Rating.builder()
                .name("R")
                .description("Лицам до 17 лет просмотр возможен только в присутствии взрослого")
                .build()
        );
        ratings.add(Rating.builder()
                .name("NC-17")
                .description("Лицам до 18 лет просмотр запрещён")
                .build()
        );

        for (Rating rating : ratings) {
            try {
                create(rating);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
    }
}
