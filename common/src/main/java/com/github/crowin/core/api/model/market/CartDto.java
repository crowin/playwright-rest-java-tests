package com.github.crowin.core.api.model.market;

import java.util.List;

public record CartDto(
        List<CartItem> items,
        Integer totalPrice
) {
}
