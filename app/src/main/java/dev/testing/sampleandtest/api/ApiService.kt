package dev.testing.sampleandtest.api

import dev.testing.sampleandtest.commons.Constants.Companion.RANDOM_URL
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET(RANDOM_URL)
    fun getCoin():Call<CoinDesk>
}