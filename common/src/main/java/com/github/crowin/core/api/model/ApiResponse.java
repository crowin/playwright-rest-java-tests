package com.github.crowin.core.api.model;

import com.github.crowin.core.api.model.market.ProductsDto;

public record ApiResponse<T>(
        T data
) {}
