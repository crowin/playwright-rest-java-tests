package com.github.crowin.core.api.model;

import java.util.List;

public record ListResponseData<T>(
        List<T> items,
        Integer current,
        Integer totalPages,
        Integer totalItems
) {
}
