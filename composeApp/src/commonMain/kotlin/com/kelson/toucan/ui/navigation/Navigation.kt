package com.kelson.toucan.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object PlayerSetupRoute

@Serializable
data class DeckSelectionRoute(val playersJson: String)

@Serializable
data class GameRoute(val deckId: String, val playersJson: String)
