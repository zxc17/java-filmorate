package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
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
        dbStorageUtil.updateTable("FRIENDS", "USER_ID", userId, "FRIEND_ID", friendsIds);
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
