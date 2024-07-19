package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.util.logger.LoggerMessagePattern;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class BaseService<T> {

    private final Storage<T> storage;

    protected BaseService(Storage<T> storage) {
        this.storage = storage;
    }

    public List<T> findAll() {
        log.debug(LoggerMessagePattern.DEBUG, "findAll", null);
        try {
            return storage.findAll();
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "findAll", null, e.getMessage(), e.getClass());
            throw e;
        }
    }

    public Optional<T> findById(Long id) {
        log.debug(LoggerMessagePattern.DEBUG, "findById", id);
        try {
            Optional<T> result = storage.findById(id);
            if (result.isEmpty()) {
                throw  new NotFoundException("Объект с ID: %d не найден".formatted(id));
            }
            return result;

        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "findById", id, e.getMessage(), e.getClass());
            throw e;
        }
    }

    public T create(T obj) {
        log.debug(LoggerMessagePattern.DEBUG, "create", obj);
        try {
            if (hasEqual(obj)) {
                throw new DuplicatedDataException("Такой объект уже существует");
            }
            return storage.create(obj);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "create", obj, e.getMessage(), e.getClass());
            throw e;
        }
    }

    public T update(T obj) {
        log.debug(LoggerMessagePattern.DEBUG, "update", obj);
        try {
            findById(executeMethodGetId(obj));
            return storage.update(obj);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "update", obj, e.getMessage(), e.getClass());
            throw e;
        }
    }

    public void delete(long id) {
        log.debug(LoggerMessagePattern.DEBUG, "delete", "id=%d".formatted(id));
        try {
            findById(id);
            storage.delete(id);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "delete", "id=%d".formatted(id), e.getMessage(), e.getClass());
            throw e;
        }
    }

    private boolean hasEqual(T obj) {
        log.debug(LoggerMessagePattern.DEBUG, "hasEqual", obj);
        try {
            return storage.findEqual(obj).isPresent();
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "hasEqual", obj, e.getMessage(), e.getClass());
            throw e;
        }
    }

    private long executeMethodGetId(T obj) {
        log.debug(LoggerMessagePattern.DEBUG, "executeMethodGetId", obj);
        try {
            return (long) obj.getClass().getMethod("getId").invoke(obj);
        } catch (Exception e) {
            log.warn(LoggerMessagePattern.WARN, "executeMethodGetId", obj, e.getMessage(), e.getClass());
            throw new RuntimeException(e);
        }
    }
}
