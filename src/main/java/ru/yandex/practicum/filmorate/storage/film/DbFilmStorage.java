package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.DbBaseStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Primary
@Repository
public class DbFilmStorage extends DbBaseStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = """
            SELECT f.id,
                   f.name,
                   f.description,
                   f.release_date,
                   f.duration,
                   f.rating_id,
                   l.user_id AS liked_user_id,
                   gf.genre_id,
                   g.name AS genre_name,
                   r.name AS rating_name,
                   r.description AS rating_description
            FROM films AS f
            LEFT JOIN likes AS l ON f.id = l.film_id
            LEFT JOIN genre_film AS gf ON f.id = gf.film_id
            LEFT JOIN genres AS g ON g.id = gf.genre_id
            LEFT JOIN ratings AS r ON r.id = f.rating_id
            """;
    private static final String FIND_POPULAR_QUERY = """
            SELECT sub.*,
                   l.user_id AS liked_user_id,
                   gf.genre_id AS genre_id,
                   g.name AS genre_name,
                   r.name AS rating_name,
                   r.description AS rating_description
            FROM (SELECT f.id,
                         f.name,
                         f.description,
                         f.release_date,
                         f.duration,
                         f.rating_id,
                         COUNT(l.film_id) AS liked_users_count
                  FROM films AS f
                  LEFT JOIN likes AS l ON f.id = l.film_id
                  GROUP BY f.id
                  ORDER BY liked_users_count DESC
                  LIMIT ?) AS sub
            LEFT JOIN likes AS l ON sub.id = l.film_id
            LEFT JOIN genre_film AS gf ON sub.id = gf.film_id
            LEFT JOIN genres AS g ON g.id = gf.genre_id
            LEFT JOIN ratings AS r ON r.id = sub.rating_id
            """;
    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY + "WHERE f.id = ?";
    private static final String CREATE_QUERY = "INSERT INTO films(name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?,  duration = ?, rating_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String ADD_LIKE_QUERY = "INSERT INTO likes (film_id, user_id, created_at) VALUES(?, ?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String ADD_GENRE_QUERY = "INSERT INTO genre_film (film_id, genre_id) VALUES(?, ?)";
    private static final String DELETE_ALL_GENRES_QUERY = "DELETE FROM genre_film WHERE film_id = ?";

    private static final ResultSetExtractor<List<Film>> filmListExtractor = new FilmListExtractor();

    @Autowired
    public DbFilmStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY, filmListExtractor);
    }

    @Override
    public List<Film> findPopular(Comparator<Film> comparator, int count) {
        return Objects.requireNonNull(findMany(FIND_POPULAR_QUERY, filmListExtractor, count)).stream()
                .sorted(comparator)
                .toList();
    }

    @Override
    public Optional<Film> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, filmListExtractor, id);
    }

    @Override
    @Transactional
    public Film create(Film film) {
        long filmId = super.create(CREATE_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration().toMinutes(),
                film.getMpa() == null ? 0 : film.getMpa().getId());

        if (filmId > 0) {
            film.setId(filmId);

            if (film.getGenres() != null) {
                Set<Genre> genresSet = new LinkedHashSet<>(film.getGenres());
                for (Genre genre : genresSet) {
                    addGenre(filmId, genre.getId());
                }
            }
        }

        return findById(filmId).orElse(null);
    }

    @Override
    @Transactional
    public Film update(Film film) {
        long filmId = film.getId();
        super.update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration().toMinutes(),
                film.getMpa().getId(),
                filmId);

        if (film.getGenres() != null) {
            deleteAllGenres(filmId);
            Set<Genre> genresSet = new LinkedHashSet<>(film.getGenres());
            for (Genre genre : genresSet) {
                addGenre(filmId, genre.getId());
            }
        }

        return findById(filmId).orElse(null);
    }

    @Override
    public void delete(long id) {
        super.delete(DELETE_QUERY, id);
    }

    @Override
    public Optional<Film> findEqual(Film film) {
        return Optional.empty();
    }

    @Override
    public Film addLike(long filmId, long userId) {
        execute(ADD_LIKE_QUERY, filmId, userId, Instant.now());
        return findById(filmId).orElse(null);
    }

    @Override
    public Film deleteLike(long filmId, long userId) {
        execute(DELETE_LIKE_QUERY, filmId, userId);
        return findById(filmId).orElse(null);
    }

    public void addGenre(long filmId, long genreId) {
        execute(ADD_GENRE_QUERY, filmId, genreId);
    }

    public void deleteAllGenres(long filmId) {
        execute(DELETE_ALL_GENRES_QUERY, filmId);
    }

    private static class FilmListExtractor implements ResultSetExtractor<List<Film>> {

        @Override
        public List<Film> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
            Map<Long, Film> idToFilm = new HashMap<>();

            while (resultSet.next()) {
                Long filmId = resultSet.getLong("id");
                if (!idToFilm.containsKey(filmId)) {
                    Film film = Film.builder()
                            .id(filmId)
                            .name(resultSet.getString("name"))
                            .description(resultSet.getString("description"))
                            .releaseDate(resultSet.getDate("release_date").toLocalDate())
                            .duration(Duration.ofMinutes(resultSet.getLong("duration")))
                            .likes(new LinkedHashSet<>())
                            .genres(new LinkedHashSet<>())
                            .build();
                    idToFilm.put(filmId, film);
                }

                Film film = idToFilm.get(filmId);
                long likedUserId = resultSet.getLong("liked_user_id");
                if (likedUserId > 0) {
                    film.getLikes().add(likedUserId);
                }

                long ratingId = resultSet.getLong("rating_id");
                if (ratingId > 0) {
                    Rating rating = Rating.builder()
                            .id(ratingId)
                            .name(resultSet.getString("rating_name"))
                            .description(resultSet.getString("rating_description"))
                            .build();
                    film.setMpa(rating);
                }

                long genreId = resultSet.getLong("genre_id");
                if (genreId > 0) {
                    Genre genre = Genre.builder()
                            .id(genreId)
                            .name(resultSet.getString("genre_name"))
                            .build();
                    film.getGenres().add(genre);
                }
            }

            return new ArrayList<>(idToFilm.values());
        }
    }
}
