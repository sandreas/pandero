package com.pilabor.pandero.service

import com.pilabor.pandero.data.model.HomeDataResponse
import com.pilabor.pandero.data.model.Song
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiServiceInterface {
    @GET("/status")
    suspend fun getSomething(): Response<Map<String, String>>
/*
    @POST("/auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("/auth/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<LoginResponse>
*/
    @GET("/home")
    suspend fun getHomeData(): Response<HomeDataResponse>

    @GET("/songs/{id}")
    suspend fun getSongById(
        @Path("id") id: String
    ): Response<Song>
}