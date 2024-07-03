package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DbBaseStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Repository
public class DbUserStorage extends DbBaseStorage<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = """
            SELECT  u.id,
                    u.name,
                    u.email,
                    u.login,
                    u.birthday,
                    f.candidate_id AS accepted_user_id
            FROM users AS u
            LEFT JOIN friends AS f
            ON u.id = f.offered_by
            """;
    private static final String FIND_BY_IDS_QUERY = FIND_ALL_QUERY + " WHERE u.id IN (?)";
    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY + " WHERE u.id = ?";
    private static final String FIND_EQUAL_QUERY = FIND_ALL_QUERY + " WHERE u.email = ?";
    private static final String CREATE_QUERY = "INSERT INTO users(name, email, login, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET name = ?, email = ?, login = ?,  birthday = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO friends(offered_by, candidate_id, offered_at) VALUES (?, ?, ?)";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friends WHERE offered_by = ? AND candidate_id = ?";

    private static final ResultSetExtractor<List<User>> userListExtractor = new UserListExtractor();

    @Autowired
    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY, userListExtractor);
    }

    @Override
    public List<User> findByIds(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        return findMany(FIND_BY_IDS_QUERY,
                userListExtractor,
                ids.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", "))
        );
    }

    @Override
    public Optional<User> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, userListExtractor, id);
    }

    @Override
    public User create(User user) {
        long id = super.create(CREATE_QUERY,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        );
        user.setId(id);

        return user;
    }

    @Override
    public User update(User user) {
        super.update(UPDATE_QUERY,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());

        return user;
    }

    @Override
    public void delete(long id) {
        super.delete(DELETE_QUERY, id);
    }

    @Override
    public User addFriend(long userId, long friendId) {
        execute(ADD_FRIEND_QUERY, userId, friendId, Instant.now());
        return findById(userId).orElse(null);
    }

    @Override
    public User deleteFriend(long userId, long friendId) {
        execute(DELETE_FRIEND_QUERY, userId, friendId);
        return findById(userId).orElse(null);
    }

    @Override
    public Optional<User> findEqual(User user) {
        List<User> users = findMany(FIND_EQUAL_QUERY, userListExtractor, user.getEmail());
        return users == null || users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
    }

    static class UserListExtractor implements ResultSetExtractor<List<User>> {

        @Override
        public List<User> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
            Map<Long, User> idToUser = new HashMap<>();

            while (resultSet.next()) {
                Long userId = resultSet.getLong("id");
                if (!idToUser.containsKey(userId)) {
                    User user = User.builder()
                            .id(userId)
                            .name(resultSet.getString("name"))
                            .email(resultSet.getString("email"))
                            .login(resultSet.getString("login"))
                            .friends(new HashSet<>())
                            .build();

                    Date birthday = resultSet.getDate("birthday");
                    if (birthday != null) {
                        user.setBirthday(birthday.toLocalDate());
                    }
                    idToUser.put(userId, user);
                }

                long friendId = resultSet.getLong("accepted_user_id");
                if (friendId > 0) {
                    idToUser.get(userId).getFriends().add(friendId);
                }
            }

            return new ArrayList<>(idToUser.values());
        }
    }
}
