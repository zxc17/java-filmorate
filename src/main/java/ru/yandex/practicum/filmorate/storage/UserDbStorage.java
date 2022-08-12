package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User u) {
        String sql = "insert into USERS(LOGIN, USER_NAME, EMAIL, BIRTHDAY) " +
                "VALUES (?, ?, ?, ?)";
        if (jdbcTemplate.update(sql, u.getLogin(), u.getName(), u.getEmail(), u.getBirthday()) == 0)
            throw new StorageException(String.format("Ошибка при добавлении в БД USERS, id=%s.", u.getId()));
        //Последняя добавленная запись имеет максимальный ID, считываем его.
        sql = "select max(USER_ID) from USERS";
        u.setId(jdbcTemplate.queryForObject(sql, Long.class));
        return u;
    }

    @Override
    public User get(long id) {
        String sql = "select * from USERS where USER_ID = ?";
        List<User> query = jdbcTemplate.query(sql, this::mapRowToUser, id);
        switch (query.size()) {
            case 0:
                return null;
            case 1:
                return query.get(0);
            default:
                throw new StorageException(String.format("Ошибка при запросе данных из БД USERS, id=%s.", id));
        }
    }

    @Override
    public List<User> getAll() {
        String sql = "select * from USERS";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User update(User u) {
        String sql = "update USERS " +
                "set LOGIN = ?, USER_NAME = ?, EMAIL = ?, BIRTHDAY = ?" +
                "where USER_ID = ?";
        if (jdbcTemplate.update(sql, u.getLogin(), u.getName(), u.getEmail(), u.getBirthday(), u.getId()) == 0)
            throw new StorageException(String.format("Ошибка при обновлении данных в БД USERS, id=%s.", u.getId()));
        return u;
    }

    @Override
    public void remove(long id) {
        String sql = "delete from USERS where USER_ID = ?";
        if (jdbcTemplate.update(sql, id) == 0)
            throw new StorageException(String.format("Ошибка при удалении из БД USERS, id=%s.", id));
    }

    @Override
    public void clear() {
        String sql = "delete from USERS";
        jdbcTemplate.update(sql);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("USER_ID"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("USER_NAME"))
                .email(resultSet.getString("EMAIL"))
                .birthday(resultSet.getObject("BIRTHDAY", LocalDate.class))
                .build();
    }
}
