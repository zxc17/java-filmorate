package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class Mpa {
    @NotNull
    private long id;
    // на входящем запросе может отсутствовать.
    private String name;
}
