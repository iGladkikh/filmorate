package ru.yandex.practicum.filmorate.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Set;

public class SetOfNumbersSerializer extends JsonSerializer<Set<? extends Number>> {

    @Override
    public void serialize(Set<? extends Number> set, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (set == null) {
            jsonGenerator.writeNull();
        } else {
            jsonGenerator.writeObject(set);
        }
    }
}
