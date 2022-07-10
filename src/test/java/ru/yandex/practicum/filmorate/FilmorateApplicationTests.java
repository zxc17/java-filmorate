package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

/**
 * Интеграционный тест.
 */
@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {
	private ObjectMapper objectMapper;
	private MockMvc mockMvc;
	private FilmStorage filmStorage;
	private UserStorage userStorage;

	@Autowired
	public FilmorateApplicationTests(ObjectMapper objectMapper, MockMvc mockMvc,
									 FilmStorage filmStorage, UserStorage userStorage) {
		this.objectMapper = objectMapper;
		this.mockMvc = mockMvc;
		this.filmStorage = filmStorage;
		this.userStorage = userStorage;
	}

	@Test
	@Order(10)
	void createNewUser() {

	}

	@Test
	void name() {
	}
}
