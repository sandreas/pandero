package com.pilabor.pandero.repository

import com.pilabor.pandero.data.model.HomeDataResponse
import com.pilabor.pandero.service.ApiServiceInterface
import com.pilabor.pandero.utils.Resource
import org.koin.core.annotation.Single

@Single
class HomeRepository(private val apiService: ApiServiceInterface) {

    suspend fun getHomeData(): Resource<HomeDataResponse> {
        return try {
            val response = apiService.getHomeData()
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to fetch home data")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.message}")
        }
    }
}