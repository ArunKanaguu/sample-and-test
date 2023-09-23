package dev.testing.sampleandtest.api

import com.google.gson.annotations.SerializedName


data class CoinDesk(
    @SerializedName("time")
    val time: TimeDetails,
    @SerializedName("disclaimer")
    val disclaimer:String,
    @SerializedName("chartName")
    val chartName:String,
    @SerializedName("bpi")
    val bpi: Bpi

    )

data class Bpi(
    @SerializedName("USD")
    val USD: Coins,
    @SerializedName("GBP")
    val GBP: Coins,
    @SerializedName("EUR")
    val EUR: Coins,
)

data class TimeDetails(
    @SerializedName("updated")
    val updated:String,
    @SerializedName("updatedISO")
    val updatedISO:String,
    @SerializedName("updateduk")
    val updateduk:String
)
data class Coins(
    @SerializedName("code")
    val code:String,
    @SerializedName("symbol")
    val symbol:String,
    @SerializedName("rate")
    val rate:String,
    @SerializedName("description")
    val description:String,
    @SerializedName("rate_float")
    val rate_float:String
)

