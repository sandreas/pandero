package com.pilabor.pandero.repository

import com.pilabor.pandero.data.TempService
import com.pilabor.pandero.service.ApiServiceInterface
import org.koin.core.annotation.Single

@Single
class StatusRepository(private val apiService: ApiServiceInterface) {
    suspend fun getStatus(): String {
        return apiService.getSomething().body()?.get("status") ?: "Failed";
    }
}