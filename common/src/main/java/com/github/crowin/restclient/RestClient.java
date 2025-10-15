package com.github.crowin.restclient;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class RestClient {
    private final String BASE_URL;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .followRedirects(true)
            .build();

    private RestClient(String baseUrl, Interceptor... interceptors) {
        BASE_URL = baseUrl;
        addInterceptors(interceptors);
    }

    public static RestClient create(String baseUrl, Interceptor... interceptors) {
        return new RestClient(baseUrl, interceptors);
    }

    public static RestClient create(Interceptor... interceptors) {
        return create("", interceptors);
    }

    public HttpRequestBuilder get(String url) {
        return new HttpRequestBuilder(client, "GET", BASE_URL + url);
    }

    public HttpRequestBuilder post(String url) {
        return new HttpRequestBuilder(client, "POST", BASE_URL + url);
    }

    public HttpRequestBuilder put(String url) {
        return new HttpRequestBuilder(client, "PUT", BASE_URL + url);
    }

    public HttpRequestBuilder delete(String url) {
        return new HttpRequestBuilder(client, "DELETE", BASE_URL + url);
    }

    private void addInterceptors(Interceptor[] interceptors) {
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                client.interceptors().add(interceptor);
            }
        }
    }
}
