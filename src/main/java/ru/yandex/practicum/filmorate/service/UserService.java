package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.ValidationDataException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
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
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;
    private final FriendsStorage friendsStorage;
    private final EventStorage eventStorage;
    private final FilmService filmService;
    private final ReviewService reviewService;

    public User add(User u) {
        if (isInvalidUser(u)) throw new ValidationDataException("Некорректные данные пользователя.");
        userStorage.add(u);
        if (u.getFriends() != null)
            friendsStorage.update(u.getId(), u.getFriends());
        return u;
    }

    public User update(User u) {
        if (isInvalidUser(u)) throw new ValidationDataException("Некорректные данные пользователя.");
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
        reviewService.updateUsefulForRemoveUser(id);
        List<Long> filmIds = likesStorage.getIdFilmsRatedByUser(id);
        userStorage.remove(id);
        filmService.updateRateForFilmList(filmIds);
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
        // Для каждого юзера выбираем фильмы с оценкой.
        Map<User, List<Film>> data = new HashMap<>();
        List<User> userList = userStorage.getAll();
        // Заполнение данных.
        userList.forEach(u -> data.put(u, filmStorage.getRatedFilmListByUser(u.getId())));
        // Вычленяем данные проверяемого юзера.
        User user = userStorage.get(userId);
        List<Film> userData = data.get(user);
        // Убираем его из общей базы.
        data.remove(user);

        // Приступаем к обработке.
        // nearestUsersData - мапа, сортированнная по количеству совпадений оценок, в порядке убывания.
        Map<Long, List<Film>> nearestUsersData = new TreeMap<>(
                Comparator.comparingLong((Long k) -> k).reversed());
        for (Map.Entry<User, List<Film>> entryData : data.entrySet()) {
            long amountOfHit = 0;
            for (Film checkedFilmForCurrentUser : entryData.getValue()) {
                // Ищем совпадение оценок.
                // Первое условие: у проверяемого юзера есть оценка того же фильма, что и у итерируемого.
                // Второе условие: разница оценок не больше 1.
                if (userData.contains(checkedFilmForCurrentUser)
                        && Math.abs(userData.get(userData.indexOf(checkedFilmForCurrentUser)).getRate()
                        - checkedFilmForCurrentUser.getRate()) <= 1)
                    amountOfHit++;
            }
            // Данные пользователя, у которого есть совпадения, сохраняем. Ключ - кол-во совпадений.
            if (amountOfHit > 0)
                nearestUsersData.put(amountOfHit, entryData.getValue());
        }
        List<Film> result = new ArrayList<>();
        // Ищем фильмы с положительными оценками, которые есть у ближайшего юзера, но не у проверяемого,
        // начиная с фильмов юзера с максимальным совпадением.
        for (Map.Entry<Long, List<Film>> e : nearestUsersData.entrySet()) {
            for (Film f : e.getValue()) {
                if (f.getRate() > 5 && !userData.contains(f))
                    result.add(f);
            }
            // Если нашли рекомендации, то выходим. Нет - переходим к списку фильмов следующего юзера.
            if (result.size() > 0) break;
        }
        filmService.loadDataIntoFilms(result);
        return result;
    }

    public List<Event> getFeedForUser(long userId) {
        if (userStorage.get(userId) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        return eventStorage.getFeedForUser(userId);
    }

    private boolean isInvalidUser(User u) {
        if (u == null ||
                u.getLogin() == null ||
                u.getEmail() == null ||
                u.getBirthday() == null ||
                u.getEmail().isBlank() || !u.getEmail().contains("@") ||
                u.getLogin().isBlank() || u.getLogin().contains(" ") ||
                u.getBirthday().isAfter(LocalDate.now())
        )
            return true;
        else {
            if (u.getName() == null || u.getName().isBlank())
                u.setName(u.getLogin());
            return false;
        }
    }
}
