package com.pilabor.pandero.ui.navigation

import kotlinx.serialization.Serializable

interface RouteInterface {}
/*

@Serializable
object OnboardingRoute : RouteInterface

@Serializable
object LoginRoute : RouteInterface

@Serializable
object RegisterRoute : RouteInterface
*/

@Serializable
object HomeRoute : RouteInterface

@Serializable
data class PlaySongRoute(val id: String) : RouteInterface