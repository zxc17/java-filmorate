package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.customExceptions.ValidationDataException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционный тест.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmorateApplicationTests {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private MvcResult mvcResult;
    private final User user1, user2, user3, user4, user5, user1Updated;
    private final Film film1, film2, film3, film4, film5, film1Updated;

    @Autowired
    public FilmorateApplicationTests(ObjectMapper objectMapper, MockMvc mockMvc,
                                     FilmStorage filmStorage, UserStorage userStorage) {
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        user1 = createUser(0, "login1", "name1", "user1@ya.ru", LocalDate.of(1999, 2, 1));
        user2 = createUser(0, "login2", "name2", "user2@ya.ru", LocalDate.of(1999, 2, 2));
        user3 = createUser(0, "login3", "name3", "user3@ya.ru", LocalDate.of(1999, 2, 3));
        user4 = createUser(0, "login4", "name4", "user4@ya.ru", LocalDate.of(1999, 2, 4));
        user5 = createUser(0, "login5", "name5", "user5@ya.ru", LocalDate.of(1999, 2, 5));
        user1Updated = createUser(1, "login1", "Updated name1", "new-email@yandex.ru", LocalDate.of(1999, 2, 1));
        film1 = createFilm(0, "name1", "description1", LocalDate.of(2001, 6, 17), 91);
        film2 = createFilm(0, "name2", "description2", LocalDate.of(2002, 6, 17), 92);
        film3 = createFilm(0, "name3", "description3", LocalDate.of(2003, 6, 17), 93);
        film4 = createFilm(0, "name4", "description4", LocalDate.of(2004, 6, 17), 94);
        film5 = createFilm(0, "name5", "description5", LocalDate.of(2005, 6, 17), 95);
        film1Updated = createFilm(1, "name1 updated", "description1 updated", LocalDate.of(2001, 6, 17), 99);
    }

    private User createUser(long id, String login, String name, String email, LocalDate birthday) {
        User user = new User();
        user.setId(id);
        user.setLogin(login);
        user.setName(name);
        user.setEmail(email);
        user.setBirthday(birthday);
        user.setFriends(new HashSet<>());
        return user;
    }

    private Film createFilm(long id, String name, String description, LocalDate releaseDate, long duration) {
        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        film.setLikes(new HashSet<>());
        return film;
    }

    private ResultActions mockPerfomPost(String path, Object o) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(o))
        );
    }

    private ResultActions mockPerfomPut(String path, Object o) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(o))
        );
    }

    private ResultActions mockPerfomGet(String path) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .get(path)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions mockPerfomDelete(String path) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .delete(path)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    @Order(10)
    void createNewUser() throws Exception {
        mvcResult = mockPerfomPost("/users", user1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("login1"))
                .andExpect(jsonPath("$.name").value("name1"))
                .andExpect(jsonPath("$.email").value("user1@ya.ru"))
                .andExpect(jsonPath("$.birthday").value("1999-02-01"))
                .andReturn();
        mockPerfomPost("/users", user2);
        mockPerfomPost("/users", user3);
        mockPerfomPost("/users", user4);
        mockPerfomPost("/users", user5);
        //Вносим изменения в образцы.
        user1.setId(1);
        user2.setId(2);
        user3.setId(3);
        user4.setId(4);
        user5.setId(5);
        //Проверяем напрямую в базе
        assertEquals(5, userStorage.getList().size());
        assertEquals(user1, userStorage.get(1));
        assertEquals(user2, userStorage.get(2));
        assertEquals(user3, userStorage.get(3));
        assertEquals(user4, userStorage.get(4));
        assertEquals(user5, userStorage.get(5));
    }

    @Test
    @Order(20)
    void createUserFailLogin() throws Exception {
        //Пробел в логине
        User user = createUser(0, "login fail", "name", "user@ya.ru", LocalDate.of(1999, 2, 10));
        mvcResult = mockPerfomPost("/users", user)
                .andExpect(status().isBadRequest())
                .andReturn();
        assertEquals(ValidationDataException.class, mvcResult.getResolvedException().getClass());
        assertEquals("Некорректные данные пользователя.", mvcResult.getResolvedException().getMessage());
        //Пустой логин
        user.setLogin("");
        mvcResult = mockPerfomPost("/users", user)
                .andExpect(status().isBadRequest())
                .andReturn();
        assertEquals(ValidationDataException.class, mvcResult.getResolvedException().getClass());
        assertEquals("Некорректные данные пользователя.", mvcResult.getResolvedException().getMessage());
    }

    @Test
    @Order(30)
    void createUserFailEmail() throws Exception {
        //Email без @
        User user = createUser(0, "login", "name", "user.ya.ru", LocalDate.of(1999, 2, 10));
        mvcResult = mockPerfomPost("/users", user)
                .andExpect(status().isBadRequest())
                .andReturn();
        assertEquals(ValidationDataException.class, mvcResult.getResolvedException().getClass());
        assertEquals("Некорректные данные пользователя.", mvcResult.getResolvedException().getMessage());
        //Пустой Email.
        user.setEmail("");
        mvcResult = mockPerfomPost("/users", user)
                .andExpect(status().isBadRequest())
                .andReturn();
        assertEquals(ValidationDataException.class, mvcResult.getResolvedException().getClass());
        assertEquals("Некорректные данные пользователя.", mvcResult.getResolvedException().getMessage());
    }

    @Test
    @Order(40)
    void createUserFailBirthday() throws Exception {
        //ДР В будущем
        User user = createUser(0, "login", "name", "user@ya.ru", LocalDate.of(2222, 2, 10));
        mvcResult = mockPerfomPost("/users", user)
                .andExpect(status().isBadRequest())
                .andReturn();
        assertEquals(ValidationDataException.class, mvcResult.getResolvedException().getClass());
        assertEquals("Некорректные данные пользователя.", mvcResult.getResolvedException().getMessage());
    }

    @Test
    @Order(50)
    void updateUserOK() throws Exception {
        mockPerfomPut("/users", user1Updated)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated name1"))
                .andExpect(jsonPath("$.email").value("new-email@yandex.ru"));
    }

    @Test
    @Order(60)
    void updateUserFailID() throws Exception {
        //Несуществующий id
        user1.setId(8);
        mvcResult = mockPerfomPut("/users", user1)
                .andExpect(status().isNotFound())
                .andReturn();
        assertEquals(ValidationNotFoundException.class, mvcResult.getResolvedException().getClass());
        assertEquals("Невозможно обновить данные пользователя, id=8 не найден.",
                mvcResult.getResolvedException().getMessage());
        user1.setId(1);
        user1.setEmail("wrong.email.ru");
        mvcResult = mockPerfomPut("/users", user1)
                .andExpect(status().isBadRequest())
                .andReturn();
        assertEquals(ValidationDataException.class, mvcResult.getResolvedException().getClass());
        assertEquals("Некорректные данные пользователя.", mvcResult.getResolvedException().getMessage());
    }

    @Test
    @Order(70)
    void getUserOK() throws Exception {
        mvcResult = mockPerfomGet("/users/1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("login1"))
                .andExpect(jsonPath("$.name").value("Updated name1"))
                .andExpect(jsonPath("$.email").value("new-email@yandex.ru"))
                .andExpect(jsonPath("$.birthday").value("1999-02-01"))
                .andReturn();
    }

    @Test
    @Order(80)
    void getUserFailID() throws Exception {
        mvcResult = mockPerfomGet("/users/8")
                .andExpect(status().isNotFound())
                .andReturn();
        assertEquals(ValidationNotFoundException.class, mvcResult.getResolvedException().getClass());
        assertEquals("userId=8 не найден.", mvcResult.getResolvedException().getMessage());
    }

    @Test
    @Order(90)
    void getUserListOK() throws Exception {
        mvcResult = mockPerfomGet("/users")
                .andExpect(status().isOk())
                .andReturn();
        user2.setId(2);
        user3.setId(3);
        user4.setId(4);
        user5.setId(5);
        JsonArray j = JsonParser.parseString(mvcResult.getResponse().getContentAsString()).getAsJsonArray();
        assertEquals(5, j.size());
        assertEquals(user1Updated, objectMapper.readValue(j.get(0).toString(), User.class));
        assertEquals(user2, objectMapper.readValue(j.get(1).toString(), User.class));
        assertEquals(user3, objectMapper.readValue(j.get(2).toString(), User.class));
        assertEquals(user4, objectMapper.readValue(j.get(3).toString(), User.class));
        assertEquals(user5, objectMapper.readValue(j.get(4).toString(), User.class));
    }

    @Test
    @Order(100)
    void addFriendOK() throws Exception {
        mockPerfomPut("/users/1/friends/2", null)
                .andExpect(status().isOk());
        mockPerfomPut("/users/1/friends/3", null)
                .andExpect(status().isOk());
        mockPerfomPut("/users/4/friends/2", null)
                .andExpect(status().isOk());
        // Проверяем всех напрямую в базе.
        assertEquals(new HashSet<>(Set.of(2L, 3L)), userStorage.get(1).getFriends());
        assertEquals(new HashSet<>(Set.of(1L, 4L)), userStorage.get(2).getFriends());
        assertEquals(new HashSet<>(Set.of(1L)), userStorage.get(3).getFriends());
        assertEquals(new HashSet<>(Set.of(2L)), userStorage.get(4).getFriends());
        // Проверяем первого юзера через GET-запрос.
        mvcResult = mockPerfomGet("/users/1")
                .andExpect(status().isOk())
                .andReturn();
        User testUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);
        assertEquals(new HashSet<>(Set.of(2L, 3L)), testUser.getFriends());
    }

    @Test
    @Order(105)
    void addFriendFailID() throws Exception {
        mvcResult = mockPerfomPut("/users/6/friends/-11", null)
                .andExpect(status().isNotFound())
                .andReturn();
        assertEquals(ValidationNotFoundException.class, mvcResult.getResolvedException().getClass());
        assertEquals("userId=6 не найден.", mvcResult.getResolvedException().getMessage());

        mvcResult = mockPerfomPut("/users/1/friends/-11", null)
                .andExpect(status().isNotFound())
                .andReturn();
        assertEquals(ValidationNotFoundException.class, mvcResult.getResolvedException().getClass());
        assertEquals("friendId=-11 не найден.", mvcResult.getResolvedException().getMessage());
    }

    @Test
    @Order(110)
    void getMutualFriendListOK() throws Exception {
        mvcResult = mockPerfomGet("/users/1/friends/common/4")
                .andExpect(status().isOk())
                .andReturn();
        JsonArray j = JsonParser.parseString(mvcResult.getResponse().getContentAsString()).getAsJsonArray();
        assertEquals(1, j.size());
        User mutualFriend = objectMapper.readValue(j.get(0).toString(), User.class);
        //Устанавливаем в образце измененные поля.
        user2.setId(2);
        user2.setFriends(mutualFriend.getFriends());
        assertEquals(user2, mutualFriend);
    }

    @Test
    @Order(115)
    void getMutualFriendListFailID() throws Exception {
        mvcResult = mockPerfomGet("/users/17/friends/common/44")
                .andExpect(status().isNotFound())
                .andReturn();
        assertEquals(ValidationNotFoundException.class, mvcResult.getResolvedException().getClass());
        assertEquals("userId=17 не найден.", mvcResult.getResolvedException().getMessage());

        mvcResult = mockPerfomGet("/users/1/friends/common/44")
                .andExpect(status().isNotFound())
                .andReturn();
        assertEquals(ValidationNotFoundException.class, mvcResult.getResolvedException().getClass());
        assertEquals("friendId=44 не найден.", mvcResult.getResolvedException().getMessage());
    }

    @Test
    @Order(120)
    void removeFriendOK() throws Exception {
        mockPerfomDelete("/users/1/friends/3")
                .andExpect(status().isOk());
        // Проверяем всех напрямую в базе.
        assertEquals(new HashSet<>(Set.of(2L)), userStorage.get(1).getFriends());
        assertEquals(new HashSet<>(Set.of(1L, 4L)), userStorage.get(2).getFriends());
        assertTrue(userStorage.get(3).getFriends().isEmpty());
        assertEquals(new HashSet<>(Set.of(2L)), userStorage.get(4).getFriends());
    }

    @Test
    @Order(130)
    void removeFriendFailID() throws Exception {
        mvcResult = mockPerfomDelete("/users/71/friends/73")
                .andExpect(status().isNotFound())
                .andReturn();
        assertEquals(ValidationNotFoundException.class, mvcResult.getResolvedException().getClass());
        assertEquals("userId=71 не найден.", mvcResult.getResolvedException().getMessage());

        mvcResult = mockPerfomDelete("/users/1/friends/73")
                .andExpect(status().isNotFound())
                .andReturn();
        assertEquals(ValidationNotFoundException.class, mvcResult.getResolvedException().getClass());
        assertEquals("friendId=73 не найден.", mvcResult.getResolvedException().getMessage());
    }

    @Test
    @Order(200)
    void addFilmOK() throws Exception {
        mvcResult = mockPerfomPost("/films", film1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name1"))
                .andExpect(jsonPath("$.description").value("description1"))
                .andExpect(jsonPath("$.releaseDate").value("2001-06-17"))
                .andExpect(jsonPath("$.duration").value("91"))
                .andReturn();
        mockPerfomPost("/films", film2);
        mockPerfomPost("/films", film3);
        mockPerfomPost("/films", film4);
        mockPerfomPost("/films", film5);
        //Вносим изменения в образцы.
        film1.setId(1);
        film2.setId(2);
        film3.setId(3);
        film4.setId(4);
        film5.setId(5);
        //Проверяем напрямую в базе
        assertEquals(5, filmStorage.getList().size());
        assertEquals(film1, filmStorage.get(1));
        assertEquals(film2, filmStorage.get(2));
        assertEquals(film3, filmStorage.get(3));
        assertEquals(film4, filmStorage.get(4));
        assertEquals(film5, filmStorage.get(5));
    }

    @Test
    @Order(205)
    void addFilmFailName() throws Exception {
        film1.setName("");
        mvcResult = mockPerfomPost("/films", film1)
                .andExpect(status().isBadRequest())
                .andReturn();
        assertEquals(ValidationDataException.class, mvcResult.getResolvedException().getClass());
        assertEquals("Некорректные данные фильма.", mvcResult.getResolvedException().getMessage());
    }

    @Test
    @Order(210)
    void addFilmFailDescription() throws Exception {
        film1.setDescription("Слишком длинный description --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        mvcResult = mockPerfomPost("/films", film1)
                .andExpect(status().isBadRequest())
                .andReturn();
        assertEquals(ValidationDataException.class, mvcResult.getResolvedException().getClass());
        assertEquals("Некорректные данные фильма.", mvcResult.getResolvedException().getMessage());
    }

    @Test
    @Order(220)
    void addFilmFailReleaseDate() throws Exception {
        film1.setReleaseDate(LocalDate.of(1880, 11, 23));
        mvcResult = mockPerfomPost("/films", film1)
                .andExpect(status().isBadRequest())
                .andReturn();
        assertEquals(ValidationDataException.class, mvcResult.getResolvedException().getClass());
        assertEquals("Некорректные данные фильма.", mvcResult.getResolvedException().getMessage());
    }

    @Test
    @Order(230)
    void addFilmFailDuration() throws Exception {
        film1.setDuration(0);
        mvcResult = mockPerfomPost("/films", film1)
                .andExpect(status().isBadRequest())
                .andReturn();
        assertEquals(ValidationDataException.class, mvcResult.getResolvedException().getClass());
        assertEquals("Некорректные данные фильма.", mvcResult.getResolvedException().getMessage());
        film1.setDuration(-1);
        mvcResult = mockPerfomPost("/films", film1)
                .andExpect(status().isBadRequest())
                .andReturn();
        assertEquals(ValidationDataException.class, mvcResult.getResolvedException().getClass());
        assertEquals("Некорректные данные фильма.", mvcResult.getResolvedException().getMessage());
    }

    @Test
    @Order(240)
    void updateFilmOK() throws Exception {
        mvcResult = mockPerfomPut("/films", film1Updated)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name1 updated"))
                .andExpect(jsonPath("$.description").value("description1 updated"))
                .andExpect(jsonPath("$.releaseDate").value("2001-06-17"))
                .andExpect(jsonPath("$.duration").value("99"))
                .andReturn();
        //Проверяем напрямую в базе.
        assertEquals(film1Updated, filmStorage.get(1));
    }

    @Test
    @Order(250)
    void updateFilmFailID() throws Exception {
        film1Updated.setId(10);
        mvcResult = mockPerfomPut("/films", film1Updated)
                .andExpect(status().isNotFound())
                .andReturn();
        assertEquals(ValidationNotFoundException.class, mvcResult.getResolvedException().getClass());
        assertEquals("Невозможно обновить данные фильма, id=10 не найден.",
                mvcResult.getResolvedException().getMessage());
    }

    @Test
    @Order(260)
    public void getFilmListOK() throws Exception {
        mvcResult = mockPerfomGet("/films")
                .andExpect(status().isOk())
                .andReturn();
        film2.setId(2);
        film3.setId(3);
        film4.setId(4);
        film5.setId(5);
        JsonArray j = JsonParser.parseString(mvcResult.getResponse().getContentAsString()).getAsJsonArray();
        assertEquals(5, j.size());
        assertEquals(film1Updated, objectMapper.readValue(j.get(0).toString(), Film.class));
        assertEquals(film2, objectMapper.readValue(j.get(1).toString(), Film.class));
        assertEquals(film3, objectMapper.readValue(j.get(2).toString(), Film.class));
        assertEquals(film4, objectMapper.readValue(j.get(3).toString(), Film.class));
        assertEquals(film5, objectMapper.readValue(j.get(4).toString(), Film.class));
    }

    @Test
    @Order(270)
    public void addFilmLikeOK() throws Exception {
        mockPerfomPut("/films/3/like/1", null)
                .andExpect(status().isOk());
        mockPerfomPut("/films/3/like/5", null)
                .andExpect(status().isOk());
        mockPerfomPut("/films/3/like/3", null)
                .andExpect(status().isOk());
        //Повтор не должен учитываться.
        mockPerfomPut("/films/3/like/1", null)
                .andExpect(status().isOk());
        mockPerfomPut("/films/3/like/2", null)
                .andExpect(status().isOk());
        mockPerfomPut("/films/2/like/1", null)
                .andExpect(status().isOk());
        mockPerfomPut("/films/2/like/4", null)
                .andExpect(status().isOk());
        mockPerfomPut("/films/4/like/4", null)
                .andExpect(status().isOk());
        //Проверяем напрямую в базе.
        assertTrue(filmStorage.get(1).getLikes().isEmpty());
        assertEquals(new HashSet<>(Set.of(1L, 4L)), filmStorage.get(2).getLikes());
        assertEquals(new HashSet<>(Set.of(1L, 2L, 3L, 5L)), filmStorage.get(3).getLikes());
        assertEquals(new HashSet<>(Set.of(4L)), filmStorage.get(4).getLikes());
        assertTrue(filmStorage.get(5).getLikes().isEmpty());
    }

    @Test
    @Order(280)
    public void addFilmLikeFailID() throws Exception {
        mvcResult = mockPerfomPut("/films/33/like/17", null)
                .andExpect(status().isNotFound())
                .andReturn();
        assertEquals(ValidationNotFoundException.class, mvcResult.getResolvedException().getClass());
        assertEquals("filmId=33 не найден.", mvcResult.getResolvedException().getMessage());

        mvcResult = mockPerfomPut("/films/3/like/17", null)
                .andExpect(status().isNotFound())
                .andReturn();
        assertEquals(ValidationNotFoundException.class, mvcResult.getResolvedException().getClass());
        assertEquals("userId=17 не найден.", mvcResult.getResolvedException().getMessage());
    }

    @Test
    @Order(290)
    void removeFilmLikeOK() throws Exception {
        mockPerfomDelete("/films/3/like/3")
                .andExpect(status().isOk());
        //Проверяем напрямую в базе.
        assertTrue(filmStorage.get(1).getLikes().isEmpty());
        assertEquals(new HashSet<>(Set.of(1L, 4L)), filmStorage.get(2).getLikes());
        assertEquals(new HashSet<>(Set.of(1L, 2L, 5L)), filmStorage.get(3).getLikes());
        assertEquals(new HashSet<>(Set.of(4L)), filmStorage.get(4).getLikes());
        assertTrue(filmStorage.get(5).getLikes().isEmpty());
    }

    @Test
    @Order(300)
    void removeFilmLikeFailID() throws Exception {
        mvcResult = mockPerfomDelete("/films/35/like/13")
                .andExpect(status().isNotFound())
                .andReturn();
        assertEquals(ValidationNotFoundException.class, mvcResult.getResolvedException().getClass());
        assertEquals("filmId=35 не найден.", mvcResult.getResolvedException().getMessage());

        mvcResult = mockPerfomDelete("/films/3/like/13")
                .andExpect(status().isNotFound())
                .andReturn();
        assertEquals(ValidationNotFoundException.class, mvcResult.getResolvedException().getClass());
        assertEquals("userId=13 не найден.", mvcResult.getResolvedException().getMessage());
    }

    @Test
    @Order(310)
    void getPopularFilmListOK() throws Exception {
        mvcResult = mockPerfomGet("/films/popular?count=3")
                .andExpect(status().isOk())
                .andReturn();
        JsonArray j = JsonParser.parseString(mvcResult.getResponse().getContentAsString()).getAsJsonArray();
        assertEquals(3, j.size());
        assertEquals(3, j.get(0).getAsJsonObject().get("id").getAsInt());
        assertEquals(2, j.get(1).getAsJsonObject().get("id").getAsInt());
        assertEquals(4, j.get(2).getAsJsonObject().get("id").getAsInt());

        mvcResult = mockPerfomGet("/films/popular?count=1")
                .andExpect(status().isOk())
                .andReturn();
        j = JsonParser.parseString(mvcResult.getResponse().getContentAsString()).getAsJsonArray();
        assertEquals(1, j.size());
        assertEquals(3, j.get(0).getAsJsonObject().get("id").getAsInt());

        // По умолчанию выдается 10, но в базе только 5.
        mvcResult = mockPerfomGet("/films/popular")
                .andExpect(status().isOk())
                .andReturn();
        j = JsonParser.parseString(mvcResult.getResponse().getContentAsString()).getAsJsonArray();
        assertEquals(5, j.size());
        assertEquals(3, j.get(0).getAsJsonObject().get("id").getAsInt());
        assertEquals(2, j.get(1).getAsJsonObject().get("id").getAsInt());
        assertEquals(4, j.get(2).getAsJsonObject().get("id").getAsInt());
        assertEquals(1, j.get(3).getAsJsonObject().get("id").getAsInt());
        assertEquals(5, j.get(4).getAsJsonObject().get("id").getAsInt());

        // Добавляем фильмы в базу, данные фильма одинаковые, но ID будут разные, воспринимаются как разные фильмы.
        mockPerfomPost("/films", film5);
        mockPerfomPost("/films", film5);
        mockPerfomPost("/films", film5);
        mockPerfomPost("/films", film5);
        mockPerfomPost("/films", film5);
        mockPerfomPost("/films", film5);
        mockPerfomPost("/films", film5); //в базе 12 фильмов.
        mvcResult = mockPerfomGet("/films/popular")
                .andExpect(status().isOk())
                .andReturn();
        j = JsonParser.parseString(mvcResult.getResponse().getContentAsString()).getAsJsonArray();
        assertEquals(10, j.size());
        assertEquals(3, j.get(0).getAsJsonObject().get("id").getAsInt());
        assertEquals(2, j.get(1).getAsJsonObject().get("id").getAsInt());
        assertEquals(4, j.get(2).getAsJsonObject().get("id").getAsInt());
        assertEquals(1, j.get(3).getAsJsonObject().get("id").getAsInt());
        assertEquals(5, j.get(4).getAsJsonObject().get("id").getAsInt());
        assertEquals(6, j.get(5).getAsJsonObject().get("id").getAsInt());
        assertEquals(7, j.get(6).getAsJsonObject().get("id").getAsInt());
        assertEquals(8, j.get(7).getAsJsonObject().get("id").getAsInt());
        assertEquals(9, j.get(8).getAsJsonObject().get("id").getAsInt());
        assertEquals(10, j.get(9).getAsJsonObject().get("id").getAsInt());
    }


    @Test
    @Order(1000)
    void removeFilm() throws Exception {
        mockPerfomDelete("/films/3")
                .andExpect(status().isOk());
        mvcResult = mockPerfomGet("/films")
                .andExpect(status().isOk())
                .andReturn();
        JsonArray j = JsonParser.parseString(mvcResult.getResponse().getContentAsString()).getAsJsonArray();
        assertEquals(11, j.size());
        assertEquals(1, j.get(0).getAsJsonObject().get("id").getAsInt());
        assertEquals(2, j.get(1).getAsJsonObject().get("id").getAsInt());
        assertEquals(4, j.get(2).getAsJsonObject().get("id").getAsInt());
        assertEquals(5, j.get(3).getAsJsonObject().get("id").getAsInt());
        assertEquals(6, j.get(4).getAsJsonObject().get("id").getAsInt());
        assertEquals(7, j.get(5).getAsJsonObject().get("id").getAsInt());
        assertEquals(8, j.get(6).getAsJsonObject().get("id").getAsInt());
        assertEquals(9, j.get(7).getAsJsonObject().get("id").getAsInt());
        assertEquals(10, j.get(8).getAsJsonObject().get("id").getAsInt());
        assertEquals(11, j.get(9).getAsJsonObject().get("id").getAsInt());
        assertEquals(12, j.get(10).getAsJsonObject().get("id").getAsInt());

    }

    @Test
    @Order(1001)
    void removeUser() throws Exception {
        mockPerfomDelete("/users/2")
                .andExpect(status().isOk());
        mvcResult = mockPerfomGet("/users")
                .andReturn();
        JsonArray j = JsonParser.parseString(mvcResult.getResponse().getContentAsString()).getAsJsonArray();
        assertEquals(4, j.size());
        assertEquals(1, j.get(0).getAsJsonObject().get("id").getAsInt());
        assertEquals(3, j.get(1).getAsJsonObject().get("id").getAsInt());
        assertEquals(4, j.get(2).getAsJsonObject().get("id").getAsInt());
        assertEquals(5, j.get(3).getAsJsonObject().get("id").getAsInt());
    }


}