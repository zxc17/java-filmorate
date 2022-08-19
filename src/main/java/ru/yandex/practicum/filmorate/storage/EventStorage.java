package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {

    void addEvent(long userId, long entityId, String eventType, String operationType);

    List<Event> getFeedForUser(long userId);
}

