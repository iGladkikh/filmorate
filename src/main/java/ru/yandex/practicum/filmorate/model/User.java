package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"email"})
public class User {
    private Long id;

    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}$")
    private String email;

    @NotBlank
    @Pattern(regexp = ".*\\S.*")
    private String login;

    private String name;

    @Past
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
