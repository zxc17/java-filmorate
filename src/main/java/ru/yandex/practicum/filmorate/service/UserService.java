package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.ValidationDataException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUserList() {
        return userStorage.getUserList();
    }

    public User getUser(long userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            log.warn("userId={} не найден.", userId);
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        }
        return user;
    }

    public User addUser(User u) {
        if (!checkUser(u)) {
            log.warn("Некорректные данные пользователя.");
            throw new ValidationDataException("Некорректные данные пользователя.");
        }
        return userStorage.addUser(u);
    }

    public User updateUser(User u) {
        if (!checkUser(u)) {
            log.warn("Некорректные данные пользователя.");
            throw new ValidationDataException("Некорректные данные пользователя.");
        }
        if (userStorage.getUser(u.getId()) == null) {
            log.warn("Невозможно обновить данные пользователя, id={} не найден.", u.getId());
            throw new ValidationNotFoundException(
                    String.format("Невозможно обновить данные пользователя, id=%s не найден.", u.getId()));
        }
        return userStorage.updateUser(u);
    }

    public void clear() {
        userStorage.clear();
    }

    public void addFriend(long userId, long friendId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            log.warn("userId={} не найден.", userId);
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        }
        User friend = userStorage.getUser(friendId);
        if (friend == null) {
            log.warn("friendId={} не найден.", friendId);
            throw new ValidationNotFoundException(String.format("friendId=%s не найден.", friendId));
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            log.warn("userId={} не найден.", userId);
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        }
        User friend = userStorage.getUser(friendId);
        if (friend == null) {
            log.warn("friendId={} не найден.", friendId);
            throw new ValidationNotFoundException(String.format("friendId=%s не найден.", friendId));
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriendList(long userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            log.warn("userId={} не найден.", userId);
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        }
        List<User> result = new ArrayList<>();
        for (Long id : user.getFriends()) {
            result.add(userStorage.getUser(id));
        }
        return result;
    }

    public List<User> getMutualFriendList(long userId, long friendId) {
        List<User> result = new ArrayList<>();
        User user = userStorage.getUser(userId);
        if (user == null) {
            log.warn("userId={} не найден.", userId);
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        }
        User friend = userStorage.getUser(friendId);
        if (friend == null) {
            log.warn("friendId={} не найден.", friendId);
            throw new ValidationNotFoundException(String.format("friendId=%s не найден.", friendId));
        }
        for(long id : user.getFriends()) {
            if (friend.getFriends().contains(id))
                result.add(userStorage.getUser(id));
        }
        return result;
    }

    private boolean checkUser(User u) {
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
            if (u. getName() == null || u.getName().isBlank())
                u.setName(u.getLogin());
            return true;
        }
    }
}
