package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

abstract class LocalHttpClient {
    public static final String HOST = "http://localhost:8080";
    public static HttpClient client;
    static ConfigurableApplicationContext springApplication;
    String randomName;

    @BeforeAll
    static void setUp() {
        springApplication = SpringApplication.run(FilmorateApplication.class, "");
        client = HttpClient.newHttpClient();
    }

    @BeforeEach
    void generateName() {
        randomName = UUID.randomUUID().toString();
    }

    @AfterAll
    static void tearDown() {
        client.close();
        SpringApplication.exit(springApplication);
    }

    public HttpResponse<String> sendGetRequest(String uri) {
        try {
            URI url = URI.create(uri);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse<String> sendPostRequest(String uri, String body) {
        try {
            URI url = URI.create(uri);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .headers("Content-type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse<String> sendPutRequest(String uri, String body) {
        try {
            URI url = URI.create(uri);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .headers("Content-type", "application/json; charset=UTF-8")
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse<String> sendDeleteRequest(String uri) {
        try {
            URI url = URI.create(uri);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}