package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class DbBaseStorage<T> {

    private final JdbcTemplate jdbcTemplate;

    protected DbBaseStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected List<T> findMany(String query, RowMapper<T> mapper) {
        return jdbcTemplate.query(query, mapper);
    }

    protected List<T> findMany(String query, ResultSetExtractor<List<T>> extractor) {
        return jdbcTemplate.query(query, extractor);
    }

    protected List<T> findMany(String query, ResultSetExtractor<List<T>> extractor, Object... params) {
        return jdbcTemplate.query(query, extractor, params);
    }

    protected Optional<T> findOne(String query, RowMapper<T> mapper, Object... params) {
        List<T> res = jdbcTemplate.query(query, mapper, params);
        return res.isEmpty() ? Optional.empty() : Optional.of(res.getFirst());
    }

    protected Optional<T> findOne(String query, ResultSetExtractor<List<T>> extractor, Object... params) {
        List<T> films = findMany(query, extractor, params);
        return films == null || films.isEmpty() ? Optional.empty() : Optional.of(films.getFirst());
    }

    protected long create(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        if (id == 0) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
        return id;
    }

    protected int update(String query, Object... params) {
        int rowsUpdated = jdbcTemplate.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
        return rowsUpdated;
    }

    public int delete(String query, long id) {
        int rowsDeleted = jdbcTemplate.update(query, id);
        if (rowsDeleted == 0) {
            throw new InternalServerException("Не удалось удалить данные");
        }
        return rowsDeleted;
    }

    protected int execute(String query, Object... params) {
        return jdbcTemplate.update(query, params);
    }
}
