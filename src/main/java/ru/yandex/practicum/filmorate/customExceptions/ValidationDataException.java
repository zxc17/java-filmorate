package ru.yandex.practicum.filmorate.customExceptions;

/**
 * Переданы некорректные данные.
 */
public class ValidationDataException extends RuntimeException {
    public ValidationDataException(String message) {
        super(message);
    }
}
