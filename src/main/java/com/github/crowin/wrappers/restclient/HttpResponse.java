package com.github.crowin.wrappers.restclient;

import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse implements AutoCloseable {
    private final Response response;

    public HttpResponse(Response response) {
        this.response = response;
    }

    public int status() {
        return response.code();
    }

    public String header(String name) {
        return response.header(name);
    }

    public Map<String, String> headers() {
        Map<String, String> map = new HashMap<>();
        response.headers().forEach(h -> map.put(h.getFirst(), h.getSecond()));
        return map;
    }


    public String asString() {
        try {
            return response.body() != null ? response.body().string() : "";
        } catch (IOException e) {
            throw new RuntimeException("Failed to read response body", e);
        } finally {
            close();
        }
    }

    public byte[] asBytes() {
        try {
            return response.body() != null ? response.body().bytes() : new byte[0];
        } catch (IOException e) {
            throw new RuntimeException("Failed to read response body", e);
        } finally {
            close();
        }
    }

    public <T> T asJson(Class<T> clazz) {
        try {
            if (response.body() == null) return null;
            String json = response.body().string();
            return JsonUtils.fromJson(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        } finally {
            close();
        }
    }

    @Override
    public void close() {
        response.close();
    }
}
