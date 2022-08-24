package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.customExceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Rate;

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
    private final static String url, login, password;

    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(PROPERTIES_PATHS.toFile()));
            url = properties.getProperty("spring.datasource.url");
            login = properties.getProperty("spring.datasource.username");
            password = properties.getProperty("spring.datasource.password");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
        try (Connection connection = DriverManager.getConnection(url, login, password);
             Statement statement = connection.createStatement();
        ) {
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

    /**
     * Метод обновления таблицы LIKES (содержит пары ID, являющиеся сложным ключом, и поле RATE).
     * Реализует обновление "один ко многим".
     *
     * @param tableName       Имя обновляемой таблицы.
     * @param primaryIdName   Имя главного ключа.
     * @param primaryId       Значение главного ключа
     * @param secondaryIdName Имя вторичного ключа.
     * @param secondaryData   Набор вторичных ключей-значений.
     */
    //TODO Продумать возможности параметризации (заменить RATE)
    static public void updateTableLikes(String tableName,
                                        String primaryIdName, long primaryId,
                                        String secondaryIdName, Set<Rate> secondaryData) {
        if (secondaryData == null)
            throw new StorageException("Ошибка при внутреннем запросе обновления. Отсутствует список.");

        try (Connection connection = DriverManager.getConnection(url, login, password);
             Statement statement = connection.createStatement();
        ) {
            connection.setAutoCommit(false);
            statement.addBatch(String.format("delete from %s where %s = %s;", tableName, primaryIdName, primaryId));
            if (secondaryData.size() > 0) {
                StringBuilder sql = new StringBuilder();
                sql.append(String.format("insert into %s (%s, %s, %s) values ",
                        tableName, primaryIdName, secondaryIdName, "RATE"));
                Iterator<Rate> sIdIterator = secondaryData.iterator();
                while (true) {
                    Rate rate = sIdIterator.next();
                    sql.append(String.format("(%s, %s, %s)", primaryId, rate.getUserId(), rate.getRate()));
                    if (sIdIterator.hasNext()) {
                        sql.append(", ");
                    } else {
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
