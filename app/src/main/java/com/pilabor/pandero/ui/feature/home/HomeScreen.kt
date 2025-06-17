package com.pilabor.pandero.ui.feature.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.pilabor.pandero.R
import com.pilabor.pandero.data.model.Album
import com.pilabor.pandero.data.model.HomeDataResponse
import com.pilabor.pandero.data.model.Song
import com.pilabor.pandero.ui.widgets.ErrorScreen
import com.pilabor.pandero.ui.widgets.LoadingScreen
import com.pilabor.pandero.ui.widgets.CustomSpacer
import com.pilabor.pandero.ui.navigation.PlaySongRoute
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import kotlin.random.Random

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = koinViewModel()) {

    LaunchedEffect(true) {
        viewModel.event.collectLatest {
            when (it) {
                is HomeEvent.showErrorMessage -> {
                    Toast.makeText(navController.context, it.message, Toast.LENGTH_SHORT).show()
                }

                is HomeEvent.onSongClick -> {
                    navController.navigate(PlaySongRoute(it.songId))
                }
            }
        }
    }

    val state = viewModel.state.collectAsStateWithLifecycle()
    when (state.value) {
        is HomeState.Loading -> {
            LoadingScreen()
        }

        is HomeState.Success -> {
            val data = (state.value as HomeState.Success).data
            HomeScreenContent(
                name = viewModel.getUserName(),
                data = data,
                onSongClicked = {
                    viewModel.onSongClicked(it.id)
                },
                onAlbumClicked = { }
            )
        }

        is HomeState.Error -> {
            val errorMessage = (state.value as HomeState.Error).message
            ErrorScreen(errorMessage, "Retry", onPrimaryButtonClicked = {})
        }
    }
}

@Composable
fun HomeScreenContent(
    name: String,
    data: HomeDataResponse,
    onSongClicked: (Song) -> Unit,
    onAlbumClicked: (Album) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        HomeHeader(name, null)
        ContinueListeningSection(data.continueListening, onSongClicked)
        CustomSpacer(16.dp)
        TopMixesSection(data.topMixes) {}
        CustomSpacer(16.dp)
        RecommendationSection(data.recommendedSongs) {}
    }

}

@Composable
fun HomeHeader(userName: String, userImage: String?) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (userImage != null) {
            AsyncImage(
                model = userImage,
                contentDescription = null,
                modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .size(48.dp)
            )
        } else {
            Image(
                painter = painterResource(R.drawable.ic_profile),
                contentDescription = null,
                modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .size(48.dp)
            )
        }

        CustomSpacer(8.dp)
        Column {
            Text(
                "Welcome Back!",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                userName,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.70f),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}


@Composable
fun ColumnScope.ContinueListeningSection(list: List<Song>, onItemClick: (Song) -> Unit) {
    Text("Continue Listening", style = MaterialTheme.typography.titleLarge)
    CustomSpacer(8.dp)
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(list, key = { it.id }) { song ->
            GridSong(
                song = song,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .weight(1f),
                onClick = onItemClick
            )
        }
    }
}

@Composable
fun GridSong(song: Song, modifier: Modifier, onClick: (Song) -> Unit) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Gray)
            .clickable { onClick(song) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Log.d("GridSong", "song: ${song.coverImage}")
        AsyncImage(
            model = song.coverImage, contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp)),
            onError = {
                Log.e("GridSong", "Error loading image: ${it.result.throwable.message}")
            }
        )
        CustomSpacer(4.dp)
        Text(
            song.title, maxLines = 1, style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Preview
@Composable
fun PreviewHomeHeader() {
    HomeHeader("John Doe", "https://example.com/user.jpg")
}

@Composable
fun TopMixesSection(list: List<Album>, onItemClick: () -> Unit) {
    Text("Top Mixes", style = MaterialTheme.typography.titleLarge)
    CustomSpacer(8.dp)
    LazyRow {
        items(list, key = { it.id }) { album ->
            GridAlbum(
                album = album,
                modifier = Modifier
                    .padding(4.dp),
                onClick = onItemClick
            )
        }
    }
}

@Composable
fun GridAlbum(
    album: Album,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(150.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Gray)
            .clickable { onClick() },
    ) {
        AsyncImage(
            model = album.coverImage, contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(8.dp)),
            onError = {
                Log.e("GridAlbum", "Error loading image: ${it.result.throwable.message}")
            }
        )
        Text(
            album.title, maxLines = 1, style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        )
        Box(
            Modifier
                .height(8.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(createRandomColor())
        )
    }
}


@Composable
fun RecommendationSection(
    list: List<Song>,
    onItemClick: () -> Unit
) {
    Text("Recommendations", style = MaterialTheme.typography.titleLarge)
    CustomSpacer(8.dp)
    LazyRow {
        items(list, key = { it.id }) { song ->
            SongRecommendationItem(
                song = song,
                modifier = Modifier
                    .padding(4.dp),
                onClick = onItemClick
            )
        }
    }
}

@Composable
fun SongRecommendationItem(song: Song, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .size(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Gray)
            .clickable { onClick() },
    ) {
        AsyncImage(
            model = song.coverImage, contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(8.dp)),
            onError = {
                Log.e("GridAlbum", "Error loading image: ${it.result.throwable.message}")
            },
            contentScale = ContentScale.FillWidth
        )
    }
}

fun createRandomColor(): Color {
    val red = Random.nextInt(100, 255)
    val green = Random.nextInt(100, 255)
    val blue = Random.nextInt(100, 255)
    return Color(red, green, blue)
}




















