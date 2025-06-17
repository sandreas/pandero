package com.pilabor.pandero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pilabor.pandero.ui.theme.PanderoTheme
import com.pilabor.pandero.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.compose.foundation.layout.Box
import androidx.navigation.compose.rememberNavController
import com.pilabor.pandero.ui.navigation.AppNavGraph
import com.pilabor.pandero.ui.navigation.HomeRoute

class MainActivity : ComponentActivity() {
    val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            PanderoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavGraph(
                            navController = rememberNavController(),
                            startDestination = HomeRoute /*if(viewModel.isUserLoggedIn()) HomeRoute else OnboardingRoute*/
                        )
                    }
                }
            }
        }
    }
}
/*
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

 */