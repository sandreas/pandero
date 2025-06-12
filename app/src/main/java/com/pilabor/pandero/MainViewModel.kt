package com.pilabor.pandero

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pilabor.pandero.repository.StatusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MainViewModel (val repository: StatusRepository) : ViewModel() {

    private val state = MutableStateFlow("")
    val status = state.asStateFlow()

    init {
        getStatus()
    }

    private fun getStatus() {
        viewModelScope.launch {
            state.value = "ok"
            /*
            val result = repository.getStatus()
            state.value = result

             */
        }
    }
}