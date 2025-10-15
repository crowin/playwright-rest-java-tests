package com.github.crowin.core.api.interceptors;

import com.github.crowin.core.api.MarketSessionsManager;
import com.github.crowin.core.api.model.User;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Slf4j
public class MarketBasicInterceptor implements Interceptor {

    private final User user;

    public MarketBasicInterceptor(User user) {
        this.user = user;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        var req = chain.request();
        var logUser = user != null ? user.username : "unauthorized";
        var reqBuilder = req.newBuilder();

        if (user != null) {
            var authHeader = MarketSessionsManager.getTokenHeader(user);
            reqBuilder.addHeader(authHeader.getKey(), authHeader.getValue());
        }

        req = reqBuilder.build();

        log.info("Request: {} {} by {}", req.method(), req.url(), logUser);

        var resp = chain.proceed(req);
        log.info("Response: {} {} {} by {}", resp.request().method(), resp.request().url(), resp.code(), logUser);

        return resp;
    }
}
