package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Review {
    private long reviewId;

    private String content;

    private Boolean isPositive;

    private Long userId;

    private Long filmId;

    private int useful;
}
