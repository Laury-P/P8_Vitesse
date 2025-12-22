package com.openclassroom.p8_vitesse.data.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EurToPoundsResponse (
    @Json(name = "eur")
    val eurRate: EurosRate
){
    @JsonClass(generateAdapter = true)
    data class EurosRate(
        @Json(name = "gbp")
        val euroToPoundsRate: Double,
    )
}
