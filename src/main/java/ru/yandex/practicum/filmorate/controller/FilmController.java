package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable long id) {
        return filmService.findById(id);
    }

    @GetMapping("/popular")
    public List<Film> findPopular(@RequestParam Optional<Integer> count) {
        return filmService.findPopular(count);
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film newFilm) {
        return filmService.update(newFilm);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive long id) {
        filmService.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable @Positive long id, @PathVariable @Positive long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable @Positive long id, @PathVariable @Positive long userId) {
        return filmService.deleteLike(id, userId);
    }
}
