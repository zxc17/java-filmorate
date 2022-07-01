package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.NestedServletException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    private void clear() throws Exception {
        mockMvc.perform(delete("/users"));
    }

    @Test
    public void userCreateOK() throws Exception {
        MvcResult mvcResult;
        User user = new User();
        String login = "login";
        String email = "email@mail.ru";
        LocalDate birthday = LocalDate.of(2000, 8, 20);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(birthday);

        mvcResult = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();
        User testUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);
        assertEquals(1, testUser.getId());
        assertEquals(login, testUser.getLogin());
        assertEquals(login, testUser.getName()); // Имя не задано, приравнивается к логину.
        assertEquals(email, testUser.getEmail());
        assertEquals(birthday, testUser.getBirthday());
    }

    @Test
    public void userCreateFailLogin() throws Exception {
        MvcResult mvcResult;
        User user = new User();
        user.setEmail("z@mail.ru");
        user.setBirthday(LocalDate.of(2000, 8, 20));

        //Пробел в логине
        user.setLogin("wrong login");
        String body = objectMapper.writeValueAsString(user);
        assertThrowsExactly(NestedServletException.class, () ->
                mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(body)));
        //Пустой логин
        user.setLogin("");
        String body2 = objectMapper.writeValueAsString(user);
        assertThrowsExactly(NestedServletException.class, () ->
                mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(body2)));
    }

    @Test
    public void userCreateFailEmail() throws Exception {
        MvcResult mvcResult;
        User user = new User();
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 8, 20));

        //Логин без @
        user.setEmail("email.mail.ru");
        String body = objectMapper.writeValueAsString(user);
        assertThrowsExactly(NestedServletException.class, () ->
                mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(body)));
        //Пустой логин
        user.setEmail("");
        String body2 = objectMapper.writeValueAsString(user);
        assertThrowsExactly(NestedServletException.class, () ->
                mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(body2)));
    }

    @Test
    public void userCreateFailBirthday() throws Exception {
        MvcResult mvcResult;
        User user = new User();
        user.setLogin("login");
        user.setEmail("email@mail.ru");

        //ДР В будущем
        user.setBirthday(LocalDate.of(2222, 8, 20));
        String body = objectMapper.writeValueAsString(user);
        assertThrowsExactly(NestedServletException.class, () ->
                mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(body)));
    }

    @Test
    public void userUpdateWrongId() throws Exception {
        MvcResult mvcResult;
        User user = new User();
        String login = "login";
        String email = "email@mail.ru";
        LocalDate birthday = LocalDate.of(2000, 8, 20);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(birthday);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();
        user.setId(0);
        String body2 = objectMapper.writeValueAsString(user);
        assertThrowsExactly(NestedServletException.class, () ->
                mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(body2)));
    }

    @Test
    public void userUpdateOK() throws Exception {
        MvcResult mvcResult;
        User user = new User();
        String login = "login";
        String email = "email@mail.ru";
        LocalDate birthday = LocalDate.of(2000, 8, 20);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(birthday);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();
        user.setId(1);
        String newName = "New Name";
        user.setName(newName);
        mvcResult = mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();
        User testUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);
        assertEquals(1, testUser.getId());
        assertEquals(login, testUser.getLogin());
        assertEquals(newName, testUser.getName());
        assertEquals(email, testUser.getEmail());
        assertEquals(birthday, testUser.getBirthday());
    }

    @Test
    public void userGetList() throws Exception {
        MvcResult mvcResult;
        User user1 = new User();
        User user2 = new User();
        String login1 = "login1";
        String name1 = "name1";
        String email1 = "email1@mail.ru";
        LocalDate birthday1 = LocalDate.of(2000, 8, 20);
        String login2 = "login2";
        String name2 = "name2";
        String email2 = "email2@mail.ru";
        LocalDate birthday2 = LocalDate.of(2002, 9, 22);
        user1.setLogin(login1);
        user1.setName(name1);
        user1.setEmail(email1);
        user1.setBirthday(birthday1);
        user2.setLogin(login2);
        user2.setName(name2);
        user2.setEmail(email2);
        user2.setBirthday(birthday2);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isOk())
                .andReturn();
        mvcResult = mockMvc.perform(get("/users")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        JsonArray j = JsonParser.parseString(mvcResult.getResponse().getContentAsString()).getAsJsonArray();
        User testUser1 = objectMapper.readValue(j.get(0).toString(), User.class);
        User testUser2 = objectMapper.readValue(j.get(1).toString(), User.class);
        assertEquals(1, testUser1.getId());
        assertEquals(login1, testUser1.getLogin());
        assertEquals(name1, testUser1.getName());
        assertEquals(email1, testUser1.getEmail());
        assertEquals(birthday1, testUser1.getBirthday());
        assertEquals(2, testUser2.getId());
        assertEquals(login2, testUser2.getLogin());
        assertEquals(name2, testUser2.getName());
        assertEquals(email2, testUser2.getEmail());
        assertEquals(birthday2, testUser2.getBirthday());
    }
}