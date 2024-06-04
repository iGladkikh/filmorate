package ru.yandex.practicum.filmorate.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.AfterOrSameDate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AfterOrSameDateValidator implements ConstraintValidator<AfterOrSameDate, LocalDate> {
    private LocalDate dateFrom;

    @Override
    public void initialize(AfterOrSameDate annotation) {
        dateFrom = LocalDate.parse(annotation.value(),
                DateTimeFormatter.ofPattern(annotation.pattern()));
    }

    @Override
    public boolean isValid(LocalDate target, ConstraintValidatorContext context) {
        return target == null || !target.isBefore(dateFrom);
    }
}
