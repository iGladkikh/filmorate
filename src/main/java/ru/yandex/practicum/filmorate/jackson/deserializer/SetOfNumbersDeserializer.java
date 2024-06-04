package ru.yandex.practicum.filmorate.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Set;

public class SetOfNumbersDeserializer extends JsonDeserializer<Set<? extends Number>> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Set<? extends Number> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return mapper.readValue(jsonParser, new TypeReference<>() {});
    }
}
