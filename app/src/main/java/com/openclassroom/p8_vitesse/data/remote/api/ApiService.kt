package com.openclassroom.p8_vitesse.data.remote.api

import com.openclassroom.p8_vitesse.data.remote.response.EurToPoundsResponse
import retrofit2.http.GET

interface ApiService {

    @GET("eur.json")
    suspend fun getEuroToPoundsRate(
    ): EurToPoundsResponse

}