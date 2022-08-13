package ru.yandex.practicum.filmorate.customExceptions;

/**
 * Запрашиваемый объект не найден.
 */
public class ValidationNotFoundException extends RuntimeException {
    public ValidationNotFoundException(String message) {
        super(message);
    }
}
