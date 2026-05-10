package com.coderych.commons.core.util.web;

import com.coderych.commons.core.model.Pair;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpUtilsTests {

    private HttpServer server;

    @AfterEach
    void tearDown() {
        HttpUtils.reset();
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void shouldSendGetAndPostRequest() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/hello", exchange -> {
            byte[] bytes = "{\"message\":\"ok\"}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(HttpStatus.OK.value(), bytes.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(bytes);
            }
        });
        server.start();

        String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/hello";

        assertEquals("{\"message\":\"ok\"}", HttpUtils.get(baseUrl));
        assertEquals(HttpStatus.OK, HttpUtils.exchange(HttpMethod.POST, baseUrl, Pair.of("a", "b"), String.class).getStatusCode());
    }

    @Test
    void shouldSendFormPostRequestWithHeaders() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/form", exchange -> {
            assertEquals("application/x-www-form-urlencoded", exchange.getRequestHeaders().getFirst("Content-Type"));
            assertEquals("token-1", exchange.getRequestHeaders().getFirst("XXL-JOB-ACCESS-TOKEN"));
            byte[] body = exchange.getRequestBody().readAllBytes();
            assertEquals("id=7", new String(body, StandardCharsets.UTF_8));
            byte[] bytes = "{\"code\":200}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(HttpStatus.OK.value(), bytes.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(bytes);
            }
        });
        server.start();

        String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/form";

        assertEquals(
                "{\"code\":200}",
                HttpUtils.postForm(baseUrl, "id=7", spec -> spec
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header("XXL-JOB-ACCESS-TOKEN", "token-1"))
        );
    }
}
