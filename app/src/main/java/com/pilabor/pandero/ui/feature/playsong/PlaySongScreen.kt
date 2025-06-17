package com.pilabor.pandero.ui.feature.playsong

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.pilabor.pandero.ui.theme.PanderoTheme
import com.pilabor.pandero.ui.widgets.CustomSpacer
import com.pilabor.pandero.ui.widgets.ErrorScreen
import com.pilabor.pandero.ui.widgets.LoadingScreen
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaySongScreen(
    songID: String,
    navController: NavController,
    viewModel: PlaySongViewModel = koinViewModel()
) {

    LaunchedEffect(true) {
        viewModel.fetchData(songID)
        viewModel.event.collectLatest {
            when (it) {
                is PlaySongEvent.showErrorMessage -> {
                    Toast.makeText(navController.context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val state = viewModel.state.collectAsStateWithLifecycle()
    when (state.value) {
        is PlaySongState.Loading -> {
            LoadingScreen()
        }

        is PlaySongState.Success -> {
            val data = (state.value as PlaySongState.Success)
            data.currentSong?.let {
                PlaySongInternal(
                    albumName = data.currentSong.genre,
                    songName = data.currentSong.title,
                    bannerImage = data.currentSong.coverImage,
                    duration = data.duration,
                    currentPosition = data.currentPosition,
                    isPlaying = data.isPlaying,
                    isBuffering = data.isBuffering,
                    error = data.error,
                    onPlayPauseClicked = { viewModel.togglePlayPause() },
                    onSeekTo = { viewModel.changeSeekBar(it) }
                )
            }

        }

        is PlaySongState.Error -> {
            val errorMessage = (state.value as PlaySongState.Error).message
            ErrorScreen(errorMessage, "Retry", onPrimaryButtonClicked = {})
        }
    }
}

@Composable
fun PlaySongInternal(
    albumName: String? = null,
    songName: String,
    bannerImage: String,
    duration: Long,
    currentPosition: Long,
    isPlaying: Boolean,
    isBuffering: Boolean,
    error: String? = null,
    onPlayPauseClicked: () -> Unit,
    onNextClicked: () -> Unit = {},
    onPreviousClicked: () -> Unit = {},
    onSeekTo: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NowPlayingHeader(name = songName)
        CustomSpacer(32.dp)
        AsyncImage(
            model = bannerImage, contentDescription = "Banner Image",
            modifier = Modifier
                .size(300.dp)

        )
        Text(
            text = songName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        )
        albumName?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            )
        }
        CustomSpacer(16.dp)
        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { onSeekTo(it.toLong()) },
            valueRange = 0f..duration.toFloat(),
            enabled = !isBuffering,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        if (isBuffering) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }



        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = formatDuration(currentPosition),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = formatDuration(duration),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
        CustomSpacer(16.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onPreviousClicked) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_media_previous),
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            IconButton(
                onClick = onPlayPauseClicked,
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .size(80.dp)
            ) {
                val ic =
                    if (isPlaying) painterResource(android.R.drawable.ic_media_pause) else painterResource(
                        android.R.drawable.ic_media_play
                    )
                Icon(
                    painter = ic,
                    contentDescription = "Play/Pause",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            IconButton(onClick = onNextClicked) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_media_next),
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

}

fun formatDuration(millis: Long): String {
    val duration = millis / 1000 // Convert milliseconds to seconds
    val minutes = (duration / 60).toInt()
    val seconds = (duration % 60).toInt()
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
fun NowPlayingHeader(name: String) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(
            "Now Playing".uppercase(), style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            name, style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1
        )
    }
}

@Preview
@Composable
fun PlaySongScreenPreview() {
    PanderoTheme(darkTheme = true) {
        PlaySongInternal(
            albumName = "Album Name",
            songName = "Song Name",
            bannerImage = "https://example.com/image.jpg",
            duration = 300000L,
            currentPosition = 150000L,
            isPlaying = true,
            isBuffering = false,
            onPlayPauseClicked = {},
            onSeekTo = {}
        )
    }

}