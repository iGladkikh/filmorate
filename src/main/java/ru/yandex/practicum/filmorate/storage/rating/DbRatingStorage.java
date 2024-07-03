package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.DbBaseStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class DbRatingStorage extends DbBaseStorage<Rating> implements RatingStorage {
    private static final String FIND_ALL_QUERY = "SELECT id, name, description FROM ratings";
    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY + " WHERE id = ?";
    private static final String FIND_EQUAL_QUERY = FIND_ALL_QUERY + " WHERE name = ?";
    private static final String CREATE_QUERY = "INSERT INTO ratings(name, description) VALUES (?, ?)";
    private static final String UPDATE_QUERY = "UPDATE ratings SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM ratings WHERE id = ?";

    private static final RowMapper<Rating> rowMapper = new RatingRowMapper();

    @Autowired
    protected DbRatingStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public List<Rating> findAll() {
        return findMany(FIND_ALL_QUERY, rowMapper);
    }

    @Override
    public Optional<Rating> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, rowMapper, id);
    }

    @Override
    public Rating create(Rating rating) {
        long id = super.create(CREATE_QUERY, rating.getName(), rating.getDescription());
        rating.setId(id);
        return rating;
    }

    @Override
    public Rating update(Rating rating) {
        super.update(UPDATE_QUERY, rating.getName(), rating.getId());
        return rating;
    }

    @Override
    public void delete(long id) {
        super.delete(DELETE_QUERY, id);
    }

    @Override
    public Optional<Rating> findEqual(Rating rating) {
        return findOne(FIND_EQUAL_QUERY, rowMapper, rating.getName());
    }

    static class RatingRowMapper implements RowMapper<Rating> {

        @Override
        public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Rating.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .build();
        }
    }
}
