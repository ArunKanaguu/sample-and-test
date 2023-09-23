package dev.testing.sampleandtest.api

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ApiRepository @Inject constructor(private val apiService: ApiService) {
    companion object {
        private const val TAG = "ApiRepository"
    }

    fun gatCoinDetails() = apiService.getCoin()
}