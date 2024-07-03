package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface Storage<T> {

    List<T> findAll();

    Optional<T> findById(long id);

    T create(T obj);

    T update(T obj);

    void delete(long id);

    Optional<T> findEqual(T obj);
}
