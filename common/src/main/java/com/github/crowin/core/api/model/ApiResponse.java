package com.github.crowin.core.api.model;

public record ApiResponse<T>(
        ListResponseData<T> data
) {

}
