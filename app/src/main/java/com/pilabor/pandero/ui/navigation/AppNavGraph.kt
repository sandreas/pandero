package com.pilabor.pandero.ui.navigation

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.pilabor.pandero.ui.feature.home.HomeScreen
import com.pilabor.pandero.ui.feature.playsong.PlaySongScreen


@Composable
fun AppNavGraph(navController: NavHostController, startDestination: RouteInterface) {
    NavHost(navController = navController, startDestination = startDestination) {
        /*
        composable<OnboardingRoute> {
            OnboardingScreen(
                navController
            )
        }
        composable<LoginRoute> {
            LoginScreen(navController)
        }

        composable<RegisterRoute> {
            RegisterScreen(navController)
        }

         */
        composable<HomeRoute> {
            HomeScreen(navController)
        }
        composable<PlaySongRoute> {
            val route = it.toRoute<PlaySongRoute>()
            PlaySongScreen(route.id, navController)
        }
    }
}