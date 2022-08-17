package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class Director {
    private long id;
    private String name;

}
