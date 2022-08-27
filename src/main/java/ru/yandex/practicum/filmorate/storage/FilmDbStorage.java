package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
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
        String sql = "" +
                "select * " +
                "from FILMS join MPA on FILMS.MPA_ID = MPA.MPA_ID ";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film update(Film f) {
        String sql = "" +
                "update FILMS " +
                "set FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? " +
                "where FILM_ID = ?";
        if (jdbcTemplate.update(sql,
                f.getName(), f.getDescription(), f.getReleaseDate(), f.getDuration(), f.getMpa().getId(),
                f.getId()) == 0)
            throw new StorageException(String.format("Ошибка при обновлении данных в БД FILMS, id=%s.", f.getId()));
        // Обновление жанров и др. полей с множественными значениями вызывается отдельно из модуля "сервис",
        // обрабатывается соответствующими DAO.
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
                "select FILMS.*, MPA.MPA_NAME " +
                "from FILMS " +
                "join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                "left join FILM_DIRECTOR on FILMS.FILM_ID = FILM_DIRECTOR.FILM_ID " +
                "where DIRECTOR_ID = ? " +
                "group by FILMS.FILM_ID " +
                "order by avg(FILMS.RATE) desc ";
        return jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
    }

    @Override
    public List<Film> getDirectorsFilmSortByYears(long directorId) {
        String sql = "" +
                "select FILMS.*, MPA.MPA_NAME " +
                "from FILMS " +
                "join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                "left join FILM_DIRECTOR on FILMS.FILM_ID = FILM_DIRECTOR.FILM_ID " +
                "where DIRECTOR_ID = ?" +
                "order by extract(year from RELEASE_DATE) ";
        return jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
    }

    @Override
    public List<Film> getPopularFilmsByGenre(Long genreId, Integer count) {
        String sql = "" +
                "select FILMS.*, MPA.MPA_NAME " +
                "from FILMS " +
                "join MPA on FILMS.mpa_id = MPA.mpa_id " +
                "left join FILM_GENRE on FILMS.film_id = FILM_GENRE.film_id " +
                "where FILM_GENRE.GENRE_ID = ? " +
                "group by FILMS.film_id " +
                "order by avg(FILMS.RATE) desc " +
                "limit ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, genreId, count);
    }

    @Override
    public List<Film> getPopularFilmsByYear(Integer year, Integer count) {
        String sql = "" +
                "select FILMS.*, MPA.MPA_NAME " +
                "from FILMS " +
                "join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                "where year(FILMS.RELEASE_DATE) = ? " +
                "order by FILMS.RATE desc " +
                "limit ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, year, count);
    }

    @Override
    public List<Film> getPopularFilmsByYearAndGenre(Integer year, Long genreId, Integer count) {
        String sql = "" +
                "select FILMS.*, MPA.MPA_NAME " +
                "from FILMS " +
                "join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                "left join FILM_GENRE on FILMS.FILM_ID = FILM_GENRE.FILM_ID " +
                "where year(FILMS.RELEASE_DATE) = ? and FILM_GENRE.GENRE_ID = ? " +
                "group by FILMS.FILM_ID " +
                "order by avg(FILMS.RATE) desc " +
                "limit ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, year, genreId, count);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sql = "" +
                "select FILMS.*, MPA.MPA_NAME " +
                "from FILMS " +
                "join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                "group by FILMS.film_id " +
                "order by avg(FILMS.RATE) desc " +
                "limit ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    @Override
    public double calculateRate(long id) {
        String sql = "" +
                "select avg (LIKES.RATE) as RATE " +
                "from FILMS " +
                "left join LIKES on FILMS.FILM_ID = LIKES.FILM_ID " +
                "where FILMS.FILM_ID = ? " +
                "group by FILMS.film_id ";
        Double rate = jdbcTemplate.queryForObject(sql, Double.class, id);
        return (rate == null) ? 0.0 : rate;
    }

    @Override
    public List<Film> getRatedFilmListByUser(long userId) {
        String sql = "" +
                "select * " +
                "from FILMS " +
                "join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                "left join LIKES on FILMS.FILM_ID = LIKES.FILM_ID " +
                "where USER_ID = ? " +
                "order by FILMS.RATE desc";
        return jdbcTemplate.query(sql, this::mapRowToFilm, userId);
    }

    @Override
    public double updateRate(long id, double rate) {
        String sql = "update FILMS " +
                "set RATE = ? " +
                "where FILM_ID = ?";
        if (jdbcTemplate.update(sql, rate, id) == 0)
            throw new StorageException(String.format("Ошибка при обновлении данных в БД FILMS, id=%s.", id));
        return rate;
    }

    @Override
    public List<Film> getCommonFilms(long user1Id, long user2Id) {
        String sql = "" +
                "select FILMS.*, MPA.MPA_NAME " +
                "from FILMS " +
                "join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                "join LIKES as L1 on FILMS.FILM_ID = L1.FILM_ID " +
                "join LIKES as L2 on l1.FILM_ID = L2.FILM_ID " +
                "where L1.USER_ID = ? and L2.USER_ID = ? " +
                "group by FILMS.FILM_ID " +
                "order by avg(FILMS.RATE) desc";
        return jdbcTemplate.query(sql, this::mapRowToFilm, user1Id, user2Id);
    }

    @Override
    public List<Film> searchFilmByTitle(String query) {
        String querySql = "%" + query + "%";
        String sqlQuerySearchByTitle = "" +
                "select FILMS.*, MPA.MPA_NAME " +
                "from FILMS " +
                "join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                "where lower(FILMS.FILM_NAME) like lower(?) " +
                "group by FILMS.FILM_ID " +
                "order by avg(FILMS.RATE) desc ";
        return jdbcTemplate.query(sqlQuerySearchByTitle, this::mapRowToFilm, querySql);
    }

    @Override
    public List<Film> searchFilmByDirector(String query) {
        String querySql = "%" + query + "%";
        String sqlQuerySearchByDirector = "" +
                "select FILMS.*, MPA.MPA_NAME " +
                "from FILMS " +
                "join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                "join FILM_DIRECTOR on FILM_DIRECTOR.FILM_ID = FILMS.FILM_ID " +
                "join DIRECTORS on DIRECTORS.DIRECTOR_ID = FILM_DIRECTOR.DIRECTOR_ID " +
                "where lower(DIRECTORS.DIRECTOR_NAME) like lower(?) " +
                "group by FILMS.FILM_ID " +
                "order by avg(FILMS.RATE) desc ";
        return jdbcTemplate.query(sqlQuerySearchByDirector, this::mapRowToFilm, querySql);
    }

    @Override
    public List<Film> searchFilmByTitleAndDirector(String query) {
        String querySql = "%" + query + "%";
        String sqlQuerySearchByTitleAndDirector = "" +
                "select FILMS.*, MPA.MPA_NAME " +
                "from FILMS " +
                "join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                "left join FILM_DIRECTOR on FILM_DIRECTOR.FILM_ID = FILMS.FILM_ID " +
                "left join DIRECTORS on DIRECTORS.DIRECTOR_ID = FILM_DIRECTOR.DIRECTOR_ID " +
                "where lower(DIRECTORS.DIRECTOR_NAME) like lower(?) or lower(FILMS.FILM_NAME) like lower(?)" +
                "group by FILMS.film_id " +
                "order by avg(FILMS.RATE) desc ";
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
                .rate(resultSet.getDouble("RATE"))
                .build();
    }
}
