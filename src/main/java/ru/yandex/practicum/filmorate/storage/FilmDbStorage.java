package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film add(Film f) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Map<String, Object> values = new HashMap<>();
        values.put("FILM_NAME", f.getName());
        values.put("DESCRIPTION", f.getDescription());
        values.put("RELEASE_DATE", f.getReleaseDate());
        values.put("DURATION", f.getDuration());
        values.put("MPA_ID", f.getMpa().getId());
        KeyHolder keyHolder = jdbcInsert
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FilM_ID")
                .executeAndReturnKeyHolder(values);
        f.setId(keyHolder.getKey().longValue());
        return f;
    }

    @Override
    public Film get(long id) {
        String sql = "select * " +
                "from FILMS " +
                "join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                "where FILM_ID = ?";
        List<Film> query = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        switch (query.size()) {
            case 0:
                return null;
            case 1:
                return query.get(0);
            default:
                throw new StorageException(String.format("Ошибка при запросе данных из БД FILMS, id=%s.", id));
        }
    }

    @Override
    public List<Film> getAll() {
        String sql = "select * " +
                "from FILMS join MPA on FILMS.MPA_ID = MPA.MPA_ID ";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film update(Film f) {
        String sql = "update FILMS " +
                "set FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? " +
                "where FILM_ID = ?";
        if (jdbcTemplate.update(sql,
                f.getName(), f.getDescription(), f.getReleaseDate(), f.getDuration(), f.getMpa().getId(),
                f.getId()) == 0)
            throw new StorageException(String.format("Ошибка при обновлении данных в БД FILMS, id=%s.", f.getId()));
        // Обновление жанров вызывается отдельно из модуля "сервис", обрабатывается FilmGenreStorage.
        return f;
    }

    @Override
    public void remove(long id) {
        String sql = "delete from FILMS where FILM_ID = ?";
        if (jdbcTemplate.update(sql, id) == 0)
            throw new StorageException(String.format("Ошибка при удалении из БД FILMS, id=%s.", id));
    }

    @Override
    public void clear() {
        String sql = "delete from FILMS";
        jdbcTemplate.update(sql);
    }

    @Override
    public List<Film> getDirectorsFilmSortByLikes(long directorId) {
        String sql = "" +
                "select F.*, MPA_NAME " +
                "from FILMS F " +
                "join MPA M on F.MPA_ID = M.MPA_ID " +
                "left join FILM_DIRECTOR FD on F.FILM_ID = FD.FILM_ID " +
                "left join LIKES L on F.FILM_ID = L.FILM_ID " +
                "where DIRECTOR_ID = ? " +
                "group by F.FILM_ID " +
                "order by count (L.USER_ID) DESC ";
        return jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
    }

    @Override
    public List<Film> getDirectorsFilmSortByYears(long directorId) {
        String sql = "select f.*, m.* " +
                "from FILMS f " +
                "join MPA m on f.MPA_ID = m.MPA_ID " +
                "left join FILM_DIRECTOR FD on f.FILM_ID = FD.FILM_ID " +
                "where DIRECTOR_ID = ?" +
                "order by EXTRACT(YEAR FROM RELEASE_DATE) ";
        return jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
    }

    @Override
    public List<Film> getPopularFilmsByGenre(Long genreId, Integer count) {
        String sql = "SELECT FILMS.*, MPA_NAME FROM FILMS " +
                "JOIN MPA ON FILMS.mpa_id = MPA.mpa_id " +
                "LEFT JOIN FILM_GENRE ON FILMS.film_id = FILM_GENRE.film_id " +
                "LEFT JOIN LIKES ON FILM_GENRE.film_id = LIKES.film_id " +
                "WHERE FILM_GENRE.GENRE_ID = ? " +
                "GROUP BY FILMS.film_id " +
                "ORDER BY COUNT (LIKES.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, genreId, count);
    }

    @Override
    public List<Film> getPopularFilmsByYear(Integer year, Integer count) {
        String sql = "SELECT FILMS.*, MPA_NAME FROM FILMS " +
                "JOIN MPA ON FILMS.mpa_id = MPA.mpa_id " +
                "LEFT JOIN LIKES ON FILMS.film_id = LIKES.film_id " +
                "WHERE YEAR(FILMS.RELEASE_DATE) = ?" +
                "GROUP BY FILMS.film_id " +
                "ORDER BY COUNT (LIKES.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, year, count);
    }

    @Override
    public List<Film> getPopularFilmsByYearAndGenre(Integer year, Long genreId, Integer count) {
        String sql = "SELECT FILMS.*, MPA_NAME FROM FILMS " +
                "JOIN MPA ON FILMS.mpa_id = MPA.mpa_id " +
                "LEFT JOIN FILM_GENRE ON FILMS.film_id = FILM_GENRE.film_id " +
                "LEFT JOIN LIKES ON FILM_GENRE.film_id = LIKES.film_id " +
                "WHERE YEAR(FILMS.RELEASE_DATE) = ? AND FILM_GENRE.GENRE_ID = ? " +
                "GROUP BY FILMS.film_id " +
                "ORDER BY COUNT (LIKES.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, year, genreId, count);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sql = "" +
                "SELECT FILMS.*, MPA_NAME " +
                "FROM FILMS " +
                "JOIN MPA ON FILMS.mpa_id = MPA.mpa_id " +
                "LEFT JOIN LIKES ON FILMS.film_id = LIKES.film_id " +
                "GROUP BY FILMS.film_id " +
                "ORDER BY COUNT (LIKES.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    @Override
    public List<Film> getCommonFilms(long user1Id, long user2Id) {
        String sql = "SELECT f.*, m.* FROM FILMS AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "JOIN (SELECT FILM_ID, COUNT(*) AS c " +
                "FROM LIKES GROUP BY FILM_ID) AS s ON f.FILM_ID = s.FILM_ID " +
                "JOIN LIKES AS l1 ON f.FILM_ID = l1.FILM_ID " +
                "JOIN LIKES AS l2 ON l1.FILM_ID = l2.FILM_ID " +
                "WHERE l1.USER_ID = ? AND l2.USER_ID = ? " +
                "GROUP BY f.FILM_ID ORDER BY c DESC";
        return jdbcTemplate.query(sql, this::mapRowToFilm, user1Id, user2Id);
    }

    @Override
    public List<Film> searchFilmByTitle(String query) {
        String querySql = "%" + query + "%";
        String sqlQuerySearchByTitle = "SELECT FILMS.*, MPA_NAME FROM FILMS " +
                "JOIN MPA ON FILMS.mpa_id = MPA.mpa_id " +
                "LEFT JOIN LIKES ON FILMS.film_id = LIKES.film_id " +
                "WHERE LOWER(FILMS.FILM_NAME) LIKE LOWER(?) " +
                "GROUP BY FILMS.film_id " +
                "ORDER BY COUNT (LIKES.user_id) DESC ";
        return jdbcTemplate.query(sqlQuerySearchByTitle, this::mapRowToFilm, querySql);
    }

    @Override
    public List<Film> searchFilmByDirector(String query) {
        String querySql = "%" + query + "%";
        String sqlQuerySearchByDirector = "" +
                "SELECT FILMS.*, MPA_NAME FROM FILMS " +
                "JOIN MPA ON FILMS.mpa_id = MPA.mpa_id " +
                "LEFT JOIN LIKES ON FILMS.film_id = LIKES.film_id " +
                "JOIN FILM_DIRECTOR ON FILM_DIRECTOR.FILM_ID = LIKES.FILM_ID " +
                "JOIN DIRECTORS ON DIRECTORS.DIRECTOR_ID = FILM_DIRECTOR.DIRECTOR_ID " +
                "WHERE LOWER(DIRECTORS.DIRECTOR_NAME) LIKE LOWER(?) " +
                "GROUP BY FILMS.film_id " +
                "ORDER BY COUNT (LIKES.user_id) DESC ";
        return jdbcTemplate.query(sqlQuerySearchByDirector, this::mapRowToFilm, querySql);
    }

    @Override
    public List<Film> searchFilmByTitleAndDirector(String query) {
        String querySql = "%" + query + "%";
        String sqlQuerySearchByTitleAndDirector = "" +
                "SELECT FILMS.*,MPA_NAME FROM FILMS " +
                "JOIN MPA ON FILMS.mpa_id = MPA.mpa_id " +
                "LEFT JOIN LIKES ON FILMS.film_id = LIKES.film_id " +
                "LEFT JOIN FILM_DIRECTOR ON FILM_DIRECTOR.FILM_ID = FILMS.FILM_ID " +
                "LEFT JOIN DIRECTORS ON DIRECTORS.DIRECTOR_ID = FILM_DIRECTOR.DIRECTOR_ID " +
                "WHERE LOWER(DIRECTORS.DIRECTOR_NAME) LIKE LOWER(?) OR LOWER(FILMS.FILM_NAME) LIKE LOWER(?)" +
                "GROUP BY FILMS.film_id " +
                "ORDER BY COUNT (LIKES.user_id) DESC ";
        return jdbcTemplate.query(sqlQuerySearchByTitleAndDirector, this::mapRowToFilm, querySql, querySql);
    }

    private Film mapRowToFilm(ResultSet resultSet, int numRow) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("FILM_ID"))
                .name(resultSet.getString("FILM_NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getLong("DURATION"))
                .mpa(Mpa.builder()
                        .id(resultSet.getLong("MPA_ID"))
                        .name(resultSet.getString("MPA_NAME"))
                        .build())
                .build();
    }
}
