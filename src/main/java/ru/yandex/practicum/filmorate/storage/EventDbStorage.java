package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Primary
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addEvent(long userId, long entityId, String eventType, String operationType) {
        long timestamp = Instant.now().toEpochMilli();
        String sql = "insert into EVENTS (time_stamp, event_type, operation, user_id, entity_id) " +
                "values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, timestamp, eventType, operationType, userId, entityId);
    }

    @Override
    public List<Event> getFeedForUser(long userId) {
        String sql = "select * from EVENTS where user_id = ?";
        return new ArrayList<>(jdbcTemplate.query(sql, this::mapRowToEvent, userId));
    }

    private Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(resultSet.getLong("EVENT_ID"))
                .timestamp(resultSet.getLong("TIME_STAMP"))
                .eventType(resultSet.getString("EVENT_TYPE"))
                .operation(resultSet.getString("OPERATION"))
                .userId(resultSet.getLong("USER_ID"))
                .entityId(resultSet.getLong("ENTITY_ID"))
                .build();
    }
}