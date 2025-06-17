package com.pilabor.pandero.ui.feature.playsong

sealed class PlaySongEvent {
    data class showErrorMessage(val message: String) : PlaySongEvent()
}