package com.pilabor.pandero.ui.feature.playsong
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pilabor.pandero.data.model.Song
import com.pilabor.pandero.service.MediaPlayerService

@KoinViewModel
class PlaySongViewModel(private val repo: MusicRepository, private val context: Context) :
    ViewModel() {

    private val _state = MutableStateFlow<PlaySongState>(PlaySongState.Loading)
    val state: StateFlow<PlaySongState> = _state

    private val _event = MutableSharedFlow<PlaySongEvent>()
    val event = _event.asSharedFlow()

    private var currentSong: Song? = null
    private var playBackService: MediaPlayerService? = null
    private var isBound = false

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            p0: ComponentName?,
            binder: IBinder?
        ) {
            isBound = true
            val serviceBinder = binder as MediaPlayerService.MusicBinder
            playBackService = serviceBinder.getService()
            observePlaybackService()
            currentSong?.let {
                playBackService?.playSong(it)
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
        }

    }

    fun observePlaybackService() {
        playBackService?.playerState?.onEach {
            _state.value = PlaySongState.Success(
                isPlaying = it.isPlaying,
                currentSong = it.currentSong,
                currentPosition = it.currentPosition.coerceAtLeast(0L),
                duration = it.duration.coerceAtLeast(0L),
                error = it.error,
                isBuffering = it.isBuffering
            )
        }?.launchIn(viewModelScope)
    }

    fun togglePlayPause() {
        viewModelScope.launch {
            if (state.value is PlaySongState.Success) {
                val successState = state.value as PlaySongState.Success
                if (successState.isPlaying) {
                    playBackService?.pauseSong()
                } else {
                    playBackService?.resumeSong()
                }
            } else {
                _event.emit(PlaySongEvent.showErrorMessage("No song is currently playing"))
            }
        }
    }

    fun changeSeekBar(progress: Long) {
        viewModelScope.launch {
            if (state.value is PlaySongState.Success) {
                val successState = state.value as PlaySongState.Success
                playBackService?.seekTo(progress)
                _state.value = successState.copy(currentPosition = progress)
            } else {
                _event.emit(PlaySongEvent.showErrorMessage("No song is currently playing"))
            }
        }
    }


    fun fetchData(songID: String) {
        viewModelScope.launch {
            try {
                val response = repo.getSongById(songID) // Replace with actual song ID
                if (response is Resource.Success) {
                    _state.value = PlaySongState.Success(
                        currentSong = response.data, isPlaying = false,
                        currentPosition = 0L,
                        duration = 0L,
                        error = null,
                        isBuffering = true
                    )
                    startServiceAndBind(response.data)

                } else {
                    _state.value = PlaySongState.Error("Failed to fetch song data")
                }
            } catch (e: Exception) {
                _state.value = PlaySongState.Error("Network error: ${e.message}")
            }
        }
    }

    private fun startServiceAndBind(song: Song) {
        val intent = Intent(context, MediaPlayerService::class.java).apply {
            action = MediaPlayerService.ACTION_PLAY
            putExtra(KEY_SONG, song)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        if (!isBound) {
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

}