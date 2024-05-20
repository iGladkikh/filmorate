package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmControllerTest extends LocalHttpClient {
    static final String URI = HOST + "/films";
    ObjectNode film;

    @BeforeEach
    void setup() {
        film = new ObjectMapper().createObjectNode()
                .put("name", randomName)
                .put("description", "")
                .put("releaseDate", "1995-12-27")
                .put("duration", 180);
    }

    @Test
    void responseStatusCodeShouldBe200WhenDataIsCorrect() {
        HttpResponse<String> response = sendPostRequest(URI, film.toString());
        int statusCode = response.statusCode();

        assertEquals(200, statusCode);
    }

    @Test
    void responseStatusCodeShouldBe400WhenFilmNameIsEmpty() {
        film.put("name", "");

        HttpResponse<String> response = sendPostRequest(URI, film.toString());
        int statusCode = response.statusCode();

        assertEquals(400, statusCode);
    }

    @Test
    void responseStatusCodeShouldBe400WhenReleaseDateBeforeMinimumValue() {
        film.put("releaseDate", "1895-12-27");

        HttpResponse<String> response = sendPostRequest(URI, film.toString());
        int statusCode = response.statusCode();

        assertEquals(400, statusCode);
    }

    @Test
    void responseStatusCodeShouldBe200WhenReleaseDateHasMinimumValue() {
        film.put("releaseDate", "1895-12-28");

        HttpResponse<String> response = sendPostRequest(URI, film.toString());
        int statusCode = response.statusCode();

        assertEquals(200, statusCode);
    }

    @Test
    void responseStatusCodeShouldBe400WhenDurationIsNegative() {
        film.put("duration", -180);

        HttpResponse<String> response = sendPostRequest(URI, film.toString());
        int statusCode = response.statusCode();

        assertEquals(400, statusCode);
    }
}