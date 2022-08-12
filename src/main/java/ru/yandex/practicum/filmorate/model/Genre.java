package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class Genre {
    @NotNull
    private long id;
    // на входящем запросе может отсутствовать.
    private String name;
}
