package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;

import java.io.FileReader;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Primary
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(long userId, long friendId) {
        String sql = "insert into FRIENDS (user_id, friend_id) VALUES ( ?, ? )";
        if (jdbcTemplate.update(sql, userId, friendId) == 0) throw new StorageException(
                String.format("Ошибка при добавлении в БД FRIENDS, userID=%s, friendID=%s.", userId, friendId));
    }

    @Override
    public Set<Long> getList(long userId) {
        String sql = "select FRIEND_ID from FRIENDS where USER_ID = ?";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, this::mapRowToId, userId));
    }

    @Override
    public void update(long userId, Set<Long> friendsIds) {
        if (friendsIds == null)
            throw new StorageException("Ошибка при внутреннем запросе обновления друзей. Отсутствует список.");
        Properties properties = new Properties();
        String url, login, password;
        try {
            properties.load(new FileReader(
                    Paths.get("src", "main", "resources", "application.properties").toFile()));
            url = properties.getProperty("spring.datasource.url");
            login = properties.getProperty("spring.datasource.username");
            password = properties.getProperty("spring.datasource.password");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(url, login, password);
             Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            statement.addBatch(String.format("delete from FRIENDS where USER_ID = %s", userId));
            if (friendsIds.size() > 0) {
                StringBuilder sql = new StringBuilder("insert into FILM_GENRE (FILM_ID, GENRE_ID) values ");
                Iterator<Long> i = friendsIds.iterator();
                do {
                    long friendId = i.next();
                    sql.append(String.format("(%s, %s)", userId, friendId));
                    if (i.hasNext())
                        sql.append(", ");
                    else {
                        sql.append(";");
                        break;
                    }
                } while (true);
                statement.addBatch(sql.toString());
            }
            statement.executeBatch();
            connection.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(long userId, long friendId) {
        String sql = "delete from FRIENDS where USER_ID = ? and FRIEND_ID = ?";
        if (jdbcTemplate.update(sql, userId, friendId) == 0) throw new StorageException(
                String.format("Ошибка при удалении из БД FRIENDS, userID=%s, friendID=%s.", userId, friendId));
    }

    @Override
    public Set<Long> getCommonFriendList(long user1Id, long user2Id) {
        String sql = "select distinct F1.FRIEND_ID " +
                "from FRIENDS F1 join FRIENDS F2 on F1.FRIEND_ID = F2.FRIEND_ID " +
                "where F1.USER_ID = ? and F2.USER_ID = ?";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, this::mapRowToId, user1Id, user2Id));
    }

    private Long mapRowToId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("FRIEND_ID");
    }
}
