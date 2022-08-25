package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
@Builder
public class Film {
    private long id;
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent()
    private LocalDate releaseDate;
    @Size(min = 1)
    private long duration;
    @NotNull
    private Mpa mpa;
    private Set<Genre> genres; //Может отсутствовать.
    private Set<Long> likes;
    private Set<Director> directors;

}
