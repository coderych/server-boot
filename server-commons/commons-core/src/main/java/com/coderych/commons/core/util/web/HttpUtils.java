package com.coderych.commons.core.util.web;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.function.Consumer;

/**
 * HTTP 请求工具类，基于 Spring RestClient，支持 GET/POST/Exchange 等操作。
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpUtils {

    @Getter
    private static volatile RestClient restClient = RestClient.create();

    public static void init(RestClient restClient) {
        if (restClient != null) {
            HttpUtils.restClient = restClient;
        }
    }

    public static void reset() {
        restClient = RestClient.create();
    }

    public static String get(String url) {
        return get(url, String.class);
    }

    public static <T> T get(String url, Class<T> responseType) {
        return get(url, responseType, null);
    }

    public static <T> T get(String url, Class<T> responseType, Consumer<RestClient.RequestHeadersSpec<?>> customizer) {
        RestClient.RequestHeadersSpec<?> spec = restClient.get().uri(url);
        if (customizer != null) {
            customizer.accept(spec);
        }
        return spec.retrieve().body(responseType);
    }

    public static String post(String url, Object body) {
        return post(url, body, String.class);
    }

    public static <T> T post(String url, Object body, Class<T> responseType) {
        return post(url, body, responseType, null);
    }

    public static <T> T post(String url, Object body, Class<T> responseType, Consumer<RestClient.RequestBodySpec> customizer) {
        RestClient.RequestBodySpec spec = restClient.post().uri(url).contentType(MediaType.APPLICATION_JSON);
        if (customizer != null) {
            customizer.accept(spec);
        }
        return spec.body(body).retrieve().body(responseType);
    }

    public static String postForm(String url, String body, Consumer<RestClient.RequestBodySpec> customizer) {
        return postForm(url, body, String.class, customizer);
    }

    public static <T> T postForm(String url, String body, Class<T> responseType, Consumer<RestClient.RequestBodySpec> customizer) {
        RestClient.RequestBodySpec spec = restClient.post().uri(url).contentType(MediaType.APPLICATION_FORM_URLENCODED);
        if (customizer != null) {
            customizer.accept(spec);
        }
        return spec.body(body).retrieve().body(responseType);
    }

    public static <T> ResponseEntity<T> exchange(HttpMethod method, String url, Object body, Class<T> responseType) {
        RestClient.RequestBodySpec spec = restClient.method(method).uri(url);
        if (body != null) {
            spec.contentType(MediaType.APPLICATION_JSON).body(body);
        }
        return spec.retrieve().toEntity(responseType);
    }

    public static HttpStatusCode head(String url) {
        return restClient.head().uri(url).retrieve().toBodilessEntity().getStatusCode();
    }
}
