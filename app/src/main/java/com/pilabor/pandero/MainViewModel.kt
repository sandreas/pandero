package com.pilabor.pandero

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pilabor.pandero.data.MediaSession
import com.pilabor.pandero.repository.StatusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
@KoinViewModel
class MainViewModel(val repository: StatusRepository, val mediaSession: MediaSession) :
    ViewModel() {

    private val state = MutableStateFlow("")
    val status = state.asStateFlow()

    init {
        getStatus()
    }

    fun isUserLoggedIn(): Boolean {
        return mediaSession.getToken() != null
    }

    private fun getStatus() {
        /*
        viewModelScope.launch {
            val result = repository.getStatus()
            state.value = result
        }

         */
    }
}