package com.pilabor.pandero.ui.feature.home

import com.pilabor.pandero.data.model.HomeDataResponse

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val data: HomeDataResponse) : HomeState()
    data class Error(val message: String) : HomeState()
}