package com.kelson.toucan.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing player state across game modes.
 * Players persist throughout the app session and can be edited at any point.
 */
class PlayerRepository {
    private val _players = MutableStateFlow<List<String>>(emptyList())
    val players: StateFlow<List<String>> = _players.asStateFlow()

    fun setPlayers(players: List<String>) {
        _players.value = players
    }

    fun addPlayer(name: String): Boolean {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty() || _players.value.contains(trimmedName)) {
            return false
        }
        _players.value = _players.value + trimmedName
        return true
    }

    fun removePlayer(index: Int) {
        _players.value = _players.value.filterIndexed { i, _ -> i != index }
    }

    fun removePlayer(name: String) {
        _players.value = _players.value.filter { it != name }
    }

    fun clearPlayers() {
        _players.value = emptyList()
    }

    fun getPlayers(): List<String> = _players.value

    fun playerCount(): Int = _players.value.size

    companion object {
        // Singleton instance for app-wide player state
        private var instance: PlayerRepository? = null

        fun getInstance(): PlayerRepository {
            return instance ?: PlayerRepository().also { instance = it }
        }
    }
}
