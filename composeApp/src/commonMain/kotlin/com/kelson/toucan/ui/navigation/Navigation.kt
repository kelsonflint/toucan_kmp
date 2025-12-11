package com.kelson.toucan.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object GameModeSelectionRoute

@Serializable
data class PlayerSetupRoute(val gameModeId: String)

@Serializable
data class DeckSelectionRoute(val gameModeId: String)

@Serializable
data class GameRoute(val gameModeId: String, val deckId: String)
