package com.pilabor.pandero

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.pilabor.pandero.ui.theme.PanderoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    var isSplashScreenVisible = true;
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition { isSplashScreenVisible }
            setOnExitAnimationListener { splashScreenProvider ->
                var zoomX = ObjectAnimator.ofFloat(
                    splashScreenProvider.iconView,
                    "scaleX",
                    0.4f,
                    0f
                )
                var zoomY = ObjectAnimator.ofFloat(
                    splashScreenProvider.iconView,
                    "scaleY",
                    0.4f,
                    0f
                )
                zoomX.duration = 500
                zoomX.doOnEnd {
                    splashScreenProvider.remove()
                }
                zoomY.duration = 500;
                zoomY.doOnEnd {
                    splashScreenProvider.remove()
                }
                zoomX.start()
                zoomY.start()
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PanderoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PanderoTheme {
        Greeting("Android")
    }
}