package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.ValidationDataException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    final UserStorage userStorage;
    final FriendsStorage friendsStorage;
    final EventStorage eventStorage;
    final LikesDbStorage likesDbStorage;
    final FilmStorage filmStorage;
    final FilmService filmService;

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

    public List<Film> getRecommendation(long userId) {
        // Для каждого юзера создается мапа фильм-оценка.
        Map<User, Map<Film, Double>> data = new HashMap<>();
        List<User> userList = userStorage.getAll();
        // Заполнение данных.
        userList.forEach(u -> data.put(u, likesDbStorage.getFullListByUser(u.getId())));
        // Вычленяем данные обрабатываемого юзера.
        User user = userStorage.get(userId);
        Map<Film, Double> userData = data.get(user);
        // Убираем его из общей базы.
        data.remove(user);

        // Приступаем к обработке.
        // Мапа, сортированнная по количеству совпадений оценок, в порядке убывания.
        Map<Long, Map<Film, Double>> nearestUsersData = new TreeMap<>(
                Comparator.comparingLong((Long k) -> k).reversed());
        for (Map.Entry<User, Map<Film, Double>> entryData : data.entrySet()) {
            long amountOfHit = 0;
            for (Map.Entry<Film, Double> entryFilmForCheckedUser : entryData.getValue().entrySet()) {
                // Ищем совпадение оценок.
                // На данном этапе есть только лайки, т.е. в поле Double может быть лишь единица,
                // поэтому просто проверяем наличие.
                if (userData.containsKey(entryFilmForCheckedUser.getKey()))
                    amountOfHit++;
            }
            // Данные пользователя, у которого есть совпадения, сохраняем. Ключ - кол-во совпадений.
            if (amountOfHit > 0)
                nearestUsersData.put(amountOfHit, entryData.getValue());
        }
        List<Film> result = new ArrayList<>();
        // Ищем фильмы с лайками, которые есть у ближайшего юзера, но не у проверяемого,
        // начиная с фильмов юзера с максимальным совпадением.
        for (Map.Entry<Long, Map<Film, Double>> e : nearestUsersData.entrySet()) {
            for (Map.Entry<Film, Double> entry : e.getValue().entrySet()) {
                if (!userData.containsKey(entry.getKey()))
                    result.add(entry.getKey());
            }
            // Если нашли рекомендации, то выходим. Нет - переходим к списку фильмов следующего юзера.
            if (result.size() > 0) break;
        }
        filmService.loadDataIntoFilm(result);
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
