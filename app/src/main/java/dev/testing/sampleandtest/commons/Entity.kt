package dev.testing.sampleandtest.commons

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PlayList(
    var duration: Int = 0,
    var format: String = "",
    var id: String = "",
    var name: String = "",
    var order: Int = 0,
    var repeat: Int = 0,
    @SerializedName("source_id")
    var sourceId: String = "",
    @SerializedName("source_type")
    var sourceType: String = "",
    var thumbnail: String = "",
    var type: String = "",
    var url: String = "",
    var local_path: String? = null,
    var download_status: Boolean = false,
    var mode: String = "NONE"
)