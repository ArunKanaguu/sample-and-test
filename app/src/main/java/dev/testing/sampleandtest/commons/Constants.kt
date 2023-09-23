package dev.testing.sampleandtest.commons

import androidx.annotation.Keep

class Constants {
    companion object {
        //https://api.coindesk.com/v1/bpi/currentprice.json
        const val BASE_URL = "https://api.coindesk.com/"
        const val RANDOM_URL = "v1/bpi/currentprice.json"
    }
}


@Keep
enum class ApiStatus {
    SUCCESS,
    ERROR,
    LOADING
}