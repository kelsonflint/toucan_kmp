package com.kelson.toucan

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kelson.toucan.data.PlayerRepository
import com.kelson.toucan.domain.models.GameMode
import com.kelson.toucan.domain.orientation.rememberOrientationLock
import com.kelson.toucan.domain.sensor.rememberTiltSensor
import com.kelson.toucan.ui.navigation.*
import com.kelson.toucan.ui.screens.*
import com.kelson.toucan.ui.theme.ToucanTheme
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
            val playerRepository = remember { PlayerRepository.getInstance() }

            NavHost(
                navController = navController,
                startDestination = HomeRoute
            ) {
                composable<HomeRoute> {
                    HomeScreen(
                        onStartGame = {
                            navController.navigate(GameModeSelectionRoute)
                        }
                    )
                }

                composable<GameModeSelectionRoute> {
                    GameModeSelectionScreen(
                        onSelectMode = { mode ->
                            if (mode == GameMode.ToucanCharades) {
                                navController.navigate(DeckSelectionRoute(mode.id))
                            } else {
                                navController.navigate(PlayerSetupRoute(mode.id))
                            }
                        },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable<PlayerSetupRoute> { backStackEntry ->
                    val route: PlayerSetupRoute = backStackEntry.toRoute()
                    val gameMode = GameMode.entries.first { it.id == route.gameModeId }

                    PlayerSetupScreen(
                        gameMode = gameMode,
                        playerRepository = playerRepository,
                        onContinue = {
                            navController.navigate(DeckSelectionRoute(route.gameModeId))
                        },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable<DeckSelectionRoute> { backStackEntry ->
                    val route: DeckSelectionRoute = backStackEntry.toRoute()
                    val gameMode = GameMode.entries.first { it.id == route.gameModeId }
                    val players = playerRepository.getPlayers()

                    DeckSelectionScreen(
                        gameMode = gameMode,
                        players = players,
                        onSelectDeck = { deckId ->
                            navController.navigate(GameRoute(route.gameModeId, deckId))
                        },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable<GameRoute> { backStackEntry ->
                    val route: GameRoute = backStackEntry.toRoute()
                    val gameMode = GameMode.entries.first { it.id == route.gameModeId }
                    val players = playerRepository.getPlayers()

                    when (gameMode) {
                        GameMode.ToucanCharades -> {
                            val tiltSensor = rememberTiltSensor()
                            val orientationLock = rememberOrientationLock()

                            DisposableEffect(Unit) {
                                tiltSensor.start()
                                onDispose {
                                    tiltSensor.stop()
                                    orientationLock.unlock()
                                }
                            }

                            CharadesGameScreen(
                                deckId = route.deckId,
                                tiltDirectionFlow = tiltSensor.tiltDirection,
                                onLockOrientation = {
                                    orientationLock.lockLandscape()
                                },
                                onUnlockOrientation = {
                                    orientationLock.unlock()
                                },
                                onPlayAgain = {
                                    navController.popBackStack(DeckSelectionRoute(route.gameModeId), false)
                                },
                                onExit = {
                                    orientationLock.unlock()
                                    navController.popBackStack(HomeRoute, false)
                                }
                            )
                        }
                        else -> {
                            GameScreen(
                                gameMode = gameMode,
                                deckId = route.deckId,
                                players = players,
                                onPlayAgain = {
                                    navController.popBackStack(DeckSelectionRoute(route.gameModeId), false)
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
    }
}
