package com.github.crowin.restclient;

import lombok.SneakyThrows;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestBuilder {

    private final OkHttpClient client;
    private final String method;
    private final String url;
    private final HttpUrl.Builder urlBuilder = new HttpUrl.Builder();
    private final Map<String, String> headers = new HashMap<>();
    private String body;

    protected HttpRequestBuilder(OkHttpClient client, String method, String url) {
        this.client = client;
        this.method = method;
        this.url = url;
    }

    // Fluent API
    public HttpRequestBuilder header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public HttpRequestBuilder queryParams(Map<String, String> params) {
        params.forEach(urlBuilder::addQueryParameter);
        return this;
    }

    public HttpRequestBuilder body(String body) {
        this.body = body;
        header("Content-Type", "text/plain");
        return this;
    }

    public HttpRequestBuilder body(Object object) {
        try {
            this.body = JsonUtils.toJson(object);
            header("Content-Type", "application/json");
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize JSON", e);
        }
        return this;
    }

    // Execute
    @SneakyThrows
    @SuppressWarnings("resource")
    public HttpResponse execute() {
        RequestBody requestBody = null;

        if (!method.equals("GET") && body != null) {
            var contentType = headers.getOrDefault("Content-Type", "text/plain");
            requestBody = RequestBody.create(body, MediaType.parse(contentType));
        }

        var builder = new Request.Builder().url(url);
        headers.forEach(builder::addHeader);
        builder.method(method, requestBody);

        var response = client.newCall(builder.build()).execute();
        return new HttpResponse(response);
    }
}
