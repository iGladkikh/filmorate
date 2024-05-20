package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import java.util.Map;

abstract class Controller<T> {
    protected static final String DEBUG_LOG_PATTERN = "Action: {}, data: {}";
    protected static final String ERROR_LOG_PATTERN = "Action: {}, data: {}, message: {}, exception: {}";

    abstract Collection<T> findAll();

    abstract T create(T  entity);

    abstract T update(T entity);

    long getNextId(Map<Long, T> entities) {
        long currentMaxId = entities.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
