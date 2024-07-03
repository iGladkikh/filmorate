package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
public class FilmController extends BaseController<Film> {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        super(filmService);
        this.filmService = filmService;
    }

    @GetMapping("/popular")
    public List<Film> findPopular(@RequestParam Optional<Integer> count) {
        return filmService.findPopular(count);
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
