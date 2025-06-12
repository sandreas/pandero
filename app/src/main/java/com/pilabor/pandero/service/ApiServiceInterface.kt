package com.pilabor.pandero.service

import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceInterface {
    @GET("/status")
    suspend fun getSomething(): Response<Map<String, String>>
}