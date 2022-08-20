package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event {
    private long eventId;
    private long timestamp;
    private String eventType;
    private String operation;
    private long userId;
    private long entityId;
}
