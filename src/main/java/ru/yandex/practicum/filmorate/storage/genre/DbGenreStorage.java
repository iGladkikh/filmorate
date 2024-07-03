package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DbBaseStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class DbGenreStorage extends DbBaseStorage<Genre> implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT id, name FROM genres";
    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY + " WHERE id = ?";
    private static final String FIND_EQUAL_QUERY = FIND_ALL_QUERY + " WHERE name = ?";
    private static final String CREATE_QUERY = "INSERT INTO genres(name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE genres SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM genres WHERE id = ?";

    private static final RowMapper<Genre> rowMapper = new GenreRowMapper();

    @Autowired
    protected DbGenreStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY, rowMapper);
    }

    @Override
    public Optional<Genre> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, rowMapper, id);
    }

    @Override
    public Genre create(Genre genre) {
        long id = super.create(CREATE_QUERY, genre.getName());
        genre.setId(id);
        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        super.update(UPDATE_QUERY, genre.getName(), genre.getId());
        return genre;
    }

    @Override
    public void delete(long id) {
        super.delete(DELETE_QUERY, id);
    }

    @Override
    public Optional<Genre> findEqual(Genre genre) {
        return findOne(FIND_EQUAL_QUERY, rowMapper, genre.getName());
    }

    private static class GenreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Genre.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .build();
        }
    }
}
