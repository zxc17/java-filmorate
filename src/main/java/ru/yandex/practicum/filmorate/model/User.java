package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
@Builder
public class User {
    private long id;
    @Pattern(regexp = "^\\S*$")
    private String login;
    // Может быть null, тогда используется login
    private String name;
    @Email
    private String email;
    @PastOrPresent
    private LocalDate birthday;
    private Set<Long> friends;

}


