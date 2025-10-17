package com.github.crowin.core.api.model.market;

import java.util.List;

public record ProductsDto(
        List<ProductItem> items,
        Integer current,
        Integer totalPages,
        Integer totalItems
) {
}
