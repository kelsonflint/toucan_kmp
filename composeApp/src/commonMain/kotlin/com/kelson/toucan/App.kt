package com.kelson.toucan

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kelson.toucan.ui.navigation.*
import com.kelson.toucan.ui.screens.*
import com.kelson.toucan.ui.theme.ToucanTheme
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    ToucanTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = HomeRoute
            ) {
                composable<HomeRoute> {
                    HomeScreen(
                        onStartGame = {
                            navController.navigate(PlayerSetupRoute)
                        }
                    )
                }

                composable<PlayerSetupRoute> {
                    PlayerSetupScreen(
                        onContinue = { players ->
                            val playersJson = Json.encodeToString(players)
                            navController.navigate(DeckSelectionRoute(playersJson))
                        },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable<DeckSelectionRoute> { backStackEntry ->
                    val route: DeckSelectionRoute = backStackEntry.toRoute()
                    val players = Json.decodeFromString<List<String>>(route.playersJson)

                    DeckSelectionScreen(
                        players = players,
                        onSelectDeck = { deckId ->
                            navController.navigate(GameRoute(deckId, route.playersJson))
                        },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable<GameRoute> { backStackEntry ->
                    val route: GameRoute = backStackEntry.toRoute()
                    val players = Json.decodeFromString<List<String>>(route.playersJson)

                    GameScreen(
                        deckId = route.deckId,
                        players = players,
                        onPlayAgain = {
                            navController.popBackStack(DeckSelectionRoute(route.playersJson), false)
                        },
                        onExit = {
                            navController.popBackStack(HomeRoute, false)
                        }
                    )
                }
            }
        }
    }
}
