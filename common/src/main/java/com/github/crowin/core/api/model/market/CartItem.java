package com.github.crowin.core.api.model.market;

public record CartItem(
        ProductItem product,
        Integer quantity,
        Double totalPrice
) {}
