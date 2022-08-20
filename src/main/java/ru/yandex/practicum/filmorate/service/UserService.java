package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.ValidationDataException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    final UserStorage userStorage;
    final FriendsStorage friendsStorage;
    final EventStorage eventStorage;

    public User add(User u) {
        if (!isValidUser(u)) throw new ValidationDataException("Некорректные данные пользователя.");
        userStorage.add(u);
        if (u.getFriends() != null)
            friendsStorage.update(u.getId(), u.getFriends());
        return u;
    }

    public User update(User u) {
        if (!isValidUser(u)) throw new ValidationDataException("Некорректные данные пользователя.");
        if (userStorage.get(u.getId()) == null) throw new ValidationNotFoundException(
                String.format("Невозможно обновить данные пользователя, id=%s не найден.", u.getId()));
        userStorage.update(u);
        if (u.getFriends() != null)
            friendsStorage.update(u.getId(), u.getFriends());
        return u;
    }

    public User get(long userId) {
        User user = userStorage.get(userId);
        if (user == null) throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        user.setFriends(friendsStorage.getList(userId));
        return user;
    }

    public List<User> getAll() {
        List<User> result = userStorage.getAll();
        result.forEach(user -> user.setFriends(friendsStorage.getList(user.getId())));
        return result;
    }

    public void remove(long id) {
        if (userStorage.get(id) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", id));
        userStorage.remove(id);
    }

    public void clear() {
        userStorage.clear();
    }

    public void addFriend(long userId, long friendId) {
        if (userStorage.get(userId) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        if (userStorage.get(friendId) == null)
            throw new ValidationNotFoundException(String.format("friendId=%s не найден.", friendId));
        friendsStorage.add(userId, friendId);
        eventStorage.addEvent(userId, friendId, EventType.FRIEND, OperationType.ADD);
    }

    public void removeFriend(long userId, long friendId) {
        if (userStorage.get(userId) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        if (userStorage.get(friendId) == null)
            throw new ValidationNotFoundException(String.format("friendId=%s не найден.", friendId));
        friendsStorage.remove(userId, friendId);
        eventStorage.addEvent(userId, friendId, EventType.FRIEND, OperationType.REMOVE);
    }

    public List<User> getFriendList(long userId) {
        if (userStorage.get(userId) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        List<User> result = friendsStorage.getList(userId).stream()
                .map(userStorage::get)
                .collect(Collectors.toList());
        result.forEach(user -> user.setFriends(friendsStorage.getList(user.getId())));
        return result;
    }

    public List<User> getCommonFriendList(long userId, long friendId) {
        if (userStorage.get(userId) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        if (userStorage.get(friendId) == null)
            throw new ValidationNotFoundException(String.format("friendId=%s не найден.", friendId));
        List<User> result = friendsStorage.getCommonFriendList(userId, friendId).stream()
                .map(userStorage::get)
                .collect(Collectors.toList());
        result.forEach(user -> user.setFriends(friendsStorage.getList(user.getId())));
        return result;
    }

    public List<Event> getFeedForUser(long userId) {
        if (userStorage.get(userId) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        return eventStorage.getFeedForUser(userId);
    }

    private boolean isValidUser(User u) {
        if (u == null ||
                u.getLogin() == null ||
                u.getEmail() == null ||
                u.getBirthday() == null ||
                u.getEmail().isBlank() || !u.getEmail().contains("@") ||
                u.getLogin().isBlank() || u.getLogin().contains(" ") ||
                u.getBirthday().isAfter(LocalDate.now())
        )
            return false;
        else {
            if (u.getName() == null || u.getName().isBlank())
                u.setName(u.getLogin());
            return true;
        }
    }
}
