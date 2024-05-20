package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.convert.DurationUnit;
import ru.yandex.practicum.filmorate.annotation.AfterOrSameDate;
import ru.yandex.practicum.filmorate.jsonhandler.DurationDeserializer;
import ru.yandex.practicum.filmorate.jsonhandler.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
@EqualsAndHashCode(of = {"name", "releaseDate"})
public class Film {
    private static final String FIRST_RELEASE_DATE = "1895-12-28";

    private Long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @Past
    @AfterOrSameDate(FIRST_RELEASE_DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @DurationMin(minutes = 1)
    @DurationUnit(ChronoUnit.MINUTES)
    @JsonDeserialize(using = DurationDeserializer.class)
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;
}
