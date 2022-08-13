package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface FriendsStorage {

    void add(long userId, long friendId);

    Set<Long> getList(long userId);

    void update(long userId, Set<Long> friendsIds);

    void remove(long userId, long friendId);

    Set<Long> getCommonFriendList(long userId, long friendId);
}
