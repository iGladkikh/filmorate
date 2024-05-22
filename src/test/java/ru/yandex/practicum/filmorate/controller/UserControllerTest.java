package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest extends LocalHttpClient {
    static final String URI = HOST + "/users";
    ObjectNode user;

    @BeforeEach
    void setup() {
        user = new ObjectMapper().createObjectNode()
                .put("name", randomName)
                .put("email", randomName + "@yandex.ru")
                .put("login", randomName)
                .put("birthday", "1995-12-27");
    }

    @Test
    void responseStatusCodeShouldBe200WhenFieldsAreCorrect() {
        HttpResponse<String> response = sendPostRequest(URI, user.toString());
        int statusCode = response.statusCode();

        assertEquals(200, statusCode);
    }

    @Test
    void responseStatusCodeShouldBe200WhenNameIsEmpty() {
        user.put("name", "");

        HttpResponse<String> response = sendPostRequest(URI, user.toString());
        int statusCode = response.statusCode();

        assertEquals(200, statusCode);
    }

    @Test
    void responseStatusCodeShouldBe400WhenLoginsEmpty() {
        user.put("login", "");

        HttpResponse<String> response = sendPostRequest(URI, user.toString());
        int statusCode = response.statusCode();

        assertEquals(400, statusCode);
    }

    @Test
    void responseStatusCodeShouldBe400WhenReleaseDateInFuture() {
        user.put("birthday", "2035-12-31");

        HttpResponse<String> response = sendPostRequest(URI, user.toString());
        int statusCode = response.statusCode();

        assertEquals(400, statusCode);
    }

    @Test
    void responseStatusCodeShouldBe400WhenEmailIsIncorrect() {
        user.put("email", "qwert@");

        HttpResponse<String> response = sendPostRequest(URI, user.toString());
        int statusCode = response.statusCode();

        assertEquals(400, statusCode);
    }
}
