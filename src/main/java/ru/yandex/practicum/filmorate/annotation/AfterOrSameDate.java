package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import ru.yandex.practicum.filmorate.validation.AfterOrSameDateValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = AfterOrSameDateValidator.class)
public @interface AfterOrSameDate {
    String message() default "Date must not be before {value}";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
    String pattern() default "yyyy-MM-dd";
    String value();
}