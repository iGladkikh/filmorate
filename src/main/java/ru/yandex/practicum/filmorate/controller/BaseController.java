package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.service.BaseService;

import java.util.List;

abstract class BaseController<T> {

    private final BaseService<T> service;

    protected BaseController(BaseService<T> service) {
        this.service = service;
    }

    @GetMapping
    public List<T> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public T findById(@PathVariable long id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping
    public T create(@RequestBody @Valid T obj) {
        return service.create(obj);
    }

    @PutMapping
    public T update(@RequestBody @Valid T obj) {
        return service.update(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive long id) {
        service.delete(id);
    }
}
