package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    @Email
    private String email;
    @Pattern(regexp = "^\\S*$")
    private String login;
    // Может быть null, тогда используется login
    private String name;
    @NotNull
    private LocalDate birthday;


}
