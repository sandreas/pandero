package com.pilabor.pandero.ui.feature.home

sealed class HomeEvent {
    data class showErrorMessage(val message: String) : HomeEvent()
    data class onSongClick(val songId: String) : HomeEvent()
}