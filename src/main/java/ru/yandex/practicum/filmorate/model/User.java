package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.jackson.deserializer.SetOfNumbersDeserializer;
import ru.yandex.practicum.filmorate.jackson.serializer.SetOfNumbersSerializer;

import java.time.LocalDate;
import java.util.Set;

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

    @JsonSerialize(using = SetOfNumbersSerializer.class)
    @JsonDeserialize(using = SetOfNumbersDeserializer.class)
    private Set<Long> friends;
}
