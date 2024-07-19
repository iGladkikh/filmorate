package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"name"})
public class Rating {
    private Long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;
}
