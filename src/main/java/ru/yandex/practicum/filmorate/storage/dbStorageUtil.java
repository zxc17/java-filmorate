package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.customExceptions.StorageException;

import java.io.FileReader;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class dbStorageUtil {
    private final static Path PROPERTIES_PATHS =
            Path.of("src", "main", "resources", "application.properties");

    /**
     * Метод обновления таблицы связей (содержащей пары ID).
     * Например, таблицы для разрешения связей многие ко многим.
     * Реализует обновление "один ко многим", например, обновить список друзей конкретного пользователя.
     *
     * @param tableName       Имя обновляемой таблицы.
     * @param primaryIdName   Имя первичного ключа.
     * @param primaryId       Значение первичного ключа
     * @param secondaryIdName Имя вторичного ключа.
     * @param secondaryIds    Набор вторичных ключей.
     */
    static public void updateTable(String tableName,
                                   String primaryIdName, long primaryId,
                                   String secondaryIdName, Set<Long> secondaryIds) {

        if (secondaryIds == null)
            throw new StorageException("Ошибка при внутреннем запросе обновления. Отсутствует список.");
        Properties properties = new Properties();
        String url, login, password;
        try {
            properties.load(new FileReader(PROPERTIES_PATHS.toFile()));
            url = properties.getProperty("spring.datasource.url");
            login = properties.getProperty("spring.datasource.username");
            password = properties.getProperty("spring.datasource.password");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(url, login, password);
             Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            statement.addBatch(String.format("delete from %s where %s = %s;", tableName, primaryIdName, primaryId));
            if (secondaryIds.size() > 0) {
                StringBuilder sql = new StringBuilder();
                sql.append(String.format("insert into %s (%s, %s) values ", tableName, primaryIdName, secondaryIdName));
                Iterator<Long> sIdIterator = secondaryIds.iterator();
                while (true) {
                    long secondaryId = sIdIterator.next();
                    sql.append(String.format("(%s, %s)", primaryId, secondaryId));
                    if (sIdIterator.hasNext())
                        sql.append(", ");
                    else {
                        sql.append(";");
                        break;
                    }
                }
                statement.addBatch(sql.toString());
            }
            statement.executeBatch();
            connection.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
