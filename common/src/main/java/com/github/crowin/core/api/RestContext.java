package com.github.crowin.core.api;

import com.github.crowin.core.api.interceptors.MarketBasicInterceptor;
import com.github.crowin.core.api.model.User;
import com.github.crowin.restclient.RestClient;
import com.github.crowin.core.Config;
import lombok.SneakyThrows;

public class RestContext {
    private final User user;

    public RestContext(User user) {
        this.user = user;
    }

    public MarketApi market() {
        return createApiInstance(MarketApi.class);
    }

    @SneakyThrows
    /**
     * @param <T> The class type to create.
     * @param classType The class type to create.
     * @return An API instance of the given class type.
     * Creates an instance of the given class type. Might be extended to support other API types.
     */
    protected  <T> T createApiInstance(Class<T> classType) {
        return classType.getConstructor(RestClient.class)
                .newInstance(RestClient.create(Config.marketBackendUrl(), new MarketBasicInterceptor(user)));
    }
}
