package com.kelson.toucan.domain.models

enum class GameMode(
    val id: String,
    val displayName: String,
    val description: String,
    val minPlayers: Int,
    val maxPlayers: Int? = null // null means unlimited
) {
    ToucanDrink(
        id = "toucan_drink",
        displayName = "Toucan Drink Together",
        description = "Classic drinking game with prompts and minigames",
        minPlayers = 2
    ),
    ToucanCharades(
        id = "toucan_charades",
        displayName = "Toucan Charades",
        description = "Place the phone on your forehead, group helps you guess who or what you are!",
        minPlayers = 0
    ),
    ItTakesTou(
        id = "it_takes_tou",
        displayName = "It Takes Tou",
        description = "2-player game with progressively intimate questions to get to know each other",
        minPlayers = 2,
        maxPlayers = 2
    ),
    ToucanDiscuss(
        id = "toucan_discuss",
        displayName = "Toucan Discuss",
        description = "Deep discussion prompts to foster reflection among the group",
        minPlayers = 2
    );

    fun isValidPlayerCount(count: Int): Boolean {
        return count >= minPlayers && (maxPlayers == null || count <= maxPlayers)
    }

    fun playerCountDescription(): String {
        return when {
            maxPlayers == null -> "$minPlayers+ players"
            minPlayers == maxPlayers -> "$minPlayers players"
            else -> "$minPlayers-$maxPlayers players"
        }
    }
}
