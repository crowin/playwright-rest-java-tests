package com.github.crowin.core.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.crowin.core.api.model.ApiResponse;
import com.github.crowin.core.api.model.market.CartDto;
import com.github.crowin.core.api.model.market.ProductsDto;
import com.github.crowin.restclient.HttpResponse;
import com.github.crowin.restclient.RestClient;
import io.qameta.allure.Step;

import java.util.Map;

public class MarketApi extends BasicApi{

    public MarketApi(RestClient client) {
        super(client);
    }

    @Step("GET /market/products")
    public ApiResponse<ProductsDto> getProducts(Integer page, Integer size) {
        var params = Map.of("page", page.toString(), "size", size.toString());

        return client.get("/market/products")
                .queryParams(params)
                .execute()
                .asJson(new TypeReference<>() {});
    }

    @Step("GET /market/cart")
    public ApiResponse<CartDto> getCart() {
        return client.get("/market/cart")
                .execute()
                .asJson(new TypeReference<>() {});
    }

    @Step("DELETE /cart")
    public HttpResponse clearCart() {
        return client.delete("/cart").execute();
    }
}
