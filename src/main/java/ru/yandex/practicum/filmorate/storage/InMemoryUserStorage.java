package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public User add(User u) {
        u.setId(id++);
        users.put(u.getId(), u);
        log.info("Пользователь id={} успешно добавлен.", u.getId());
        return u;
    }

    @Override
    public User update(User u) {
        users.put(u.getId(), u);
        log.info("Данные пользователя id={} успешно обновлены.", u.getId());
        return u;
    }

    @Override
    public User get(long id) {
        return users.get(id);
    }

    @Override
    public void remove(long id) {
        users.remove(id);
        log.info("Пользователь id={} успешно удален.", id);
    }

    @Override
    public List<User> getList() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void clear() {
        users.clear();
        id = 1;
    }


}
