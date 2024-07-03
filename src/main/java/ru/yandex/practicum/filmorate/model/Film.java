package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.convert.DurationUnit;
import ru.yandex.practicum.filmorate.annotation.AfterOrSameDate;
import ru.yandex.practicum.filmorate.jackson.deserializer.DurationDeserializer;
import ru.yandex.practicum.filmorate.jackson.serializer.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor // @Builder без этой аннотации ломает десериализацию duration
@AllArgsConstructor
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

    private Set<Long> likes;

    private LinkedHashSet<Genre> genres;

    private Rating mpa;

    @JsonIgnore
    public int getLikesCount() {
        return likes.size();
    }
}
