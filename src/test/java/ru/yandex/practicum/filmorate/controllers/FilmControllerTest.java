package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.NestedServletException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FilmController.class)
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    private void clear() throws Exception {
        mockMvc.perform(delete("/films"));
    }

    @Test
    public void filmCreateOK() throws Exception {
        MvcResult mvcResult;
        Film film = new Film();
        String name = "film name";
        String description = "description";
        LocalDate releaseDate = LocalDate.of(1980, 11, 23);
        long duration = 100;
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);

        mvcResult = mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn();
        Film testFilm = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Film.class);
        assertEquals(1, testFilm.getId());
        assertEquals(name, testFilm.getName());
        assertEquals(description, testFilm.getDescription());
        assertEquals(releaseDate, testFilm.getReleaseDate());
        assertEquals(duration, testFilm.getDuration());
    }

    @Test
    public void filmCreateFailName() throws JsonProcessingException {
        Film film = new Film();
        String name = "";
        String description = "description";
        LocalDate releaseDate = LocalDate.of(1980, 11, 23);
        long duration = 100;
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);

        String body = objectMapper.writeValueAsString(film);
        assertThrowsExactly(NestedServletException.class, () ->
                mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(body)));
    }

    @Test
    public void filmCreateFailDescription() throws JsonProcessingException {
        Film film = new Film();
        String name = "film name";
        String description = "Слишком длинный description --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";
        LocalDate releaseDate = LocalDate.of(1980, 11, 23);
        long duration = 100;
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);

        String body = objectMapper.writeValueAsString(film);
        assertThrowsExactly(NestedServletException.class, () ->
                mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(body)));
    }

    @Test
    public void filmCreateFailReleaseDate() throws JsonProcessingException {
        Film film = new Film();
        String name = "film name";
        String description = "description ";
        LocalDate releaseDate = LocalDate.of(1880, 11, 23);
        long duration = 100;
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);

        String body = objectMapper.writeValueAsString(film);
        assertThrowsExactly(NestedServletException.class, () ->
                mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(body)));
    }

    @Test
    public void filmCreateFailDuration() throws JsonProcessingException {
        Film film = new Film();
        String name = "film name";
        String description = "description ";
        LocalDate releaseDate = LocalDate.of(1980, 11, 23);
        long duration = 0;
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);

        //Нулевая продолжительность.
        String body = objectMapper.writeValueAsString(film);
        assertThrowsExactly(NestedServletException.class, () ->
                mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(body)));
        //Отрицательная продолжительность.
        film.setDuration(-1);
        String body2 = objectMapper.writeValueAsString(film);
        assertThrowsExactly(NestedServletException.class, () ->
                mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(body2)));
    }

    @Test
    public void filmUpdateWrongId() throws Exception {
        MvcResult mvcResult;
        Film film = new Film();
        String name = "film name";
        String description = "description";
        LocalDate releaseDate = LocalDate.of(1980, 11, 23);
        long duration = 100;
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn();
        film.setId(0);
        String body = objectMapper.writeValueAsString(film);
        assertThrowsExactly(NestedServletException.class, () ->
                mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(body)));
    }

    @Test
    public void filmUpdateOK() throws Exception {
        MvcResult mvcResult;
        Film film = new Film();
        String name = "film name";
        String description = "description";
        LocalDate releaseDate = LocalDate.of(1980, 11, 23);
        long duration = 100;
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn();
        String newName = "New Film Name";
        film.setId(1);
        film.setName(newName);
        mvcResult = mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn();
        Film testFilm = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Film.class);
        assertEquals(1, testFilm.getId());
        assertEquals(newName, testFilm.getName());
        assertEquals(description, testFilm.getDescription());
        assertEquals(releaseDate, testFilm.getReleaseDate());
        assertEquals(duration, testFilm.getDuration());
    }

    @Test
    public void filmGetList() throws Exception {
        MvcResult mvcResult;
        Film film1 = new Film();
        String name1 = "film name1";
        String description1 = "description1";
        LocalDate releaseDate1 = LocalDate.of(1980, 11, 23);
        long duration1 = 100;
        film1.setName(name1);
        film1.setDescription(description1);
        film1.setReleaseDate(releaseDate1);
        film1.setDuration(duration1);
        Film film2 = new Film();
        String name2 = "film name2";
        String description2 = "description2";
        LocalDate releaseDate2 = LocalDate.of(2002, 2, 11);
        long duration2 = 200;
        film2.setName(name2);
        film2.setDescription(description2);
        film2.setReleaseDate(releaseDate2);
        film2.setDuration(duration2);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film1)))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film2)))
                .andExpect(status().isOk())
                .andReturn();
        mvcResult = mockMvc.perform(get("/films")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        JsonArray j = JsonParser.parseString(mvcResult.getResponse().getContentAsString()).getAsJsonArray();
        Film testFilm1 = objectMapper.readValue(j.get(0).toString(), Film.class);
        Film testFilm2 = objectMapper.readValue(j.get(1).toString(), Film.class);
        assertEquals(1, testFilm1.getId());
        assertEquals(name1, testFilm1.getName());
        assertEquals(description1, testFilm1.getDescription());
        assertEquals(releaseDate1, testFilm1.getReleaseDate());
        assertEquals(duration1, testFilm1.getDuration());
        assertEquals(2, testFilm2.getId());
        assertEquals(name2, testFilm2.getName());
        assertEquals(description2, testFilm2.getDescription());
        assertEquals(releaseDate2, testFilm2.getReleaseDate());
        assertEquals(duration2, testFilm2.getDuration());
    }
}