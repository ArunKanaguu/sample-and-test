package dev.testing.sampleandtest.api

import dev.testing.sampleandtest.commons.ApiStatus


data class Resource<out T>(
    val status: ApiStatus,
    val data: T?,
    val message: String?
) {
    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(ApiStatus.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(ApiStatus.ERROR, data, msg)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(ApiStatus.LOADING, data, null)
        }

    }
}
