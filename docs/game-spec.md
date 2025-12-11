# Toucan Game Specification

A drinking-game style mobile app built with Kotlin Multiplatform (KMP) and Compose Multiplatform.

## Overview

Toucan is a party game where players pass a device around, taking turns revealing prompts. Each prompt contains instructions, challenges, minigames, or special "virus" effects that persist for multiple turns.

## Architecture

### Project Structure
```
composeApp/src/
├── commonMain/kotlin/com/kelson/toucan/
│   ├── App.kt                      # Main Compose app with navigation
│   ├── data/
│   │   ├── DeckJson.kt             # JSON serialization models
│   │   └── DeckRepository.kt       # Deck loading and management
│   ├── domain/
│   │   ├── engine/
│   │   │   └── GameEngine.kt       # Core game logic
│   │   └── models/
│   │       ├── Prompt.kt           # Prompt types (sealed hierarchy)
│   │       ├── PromptDeck.kt       # Deck metadata + prompts
│   │       ├── PromptResult.kt     # Interpolated prompt for display
│   │       └── PromptType.kt       # Enum of prompt categories
│   └── ui/
│       ├── navigation/
│       │   └── Navigation.kt       # Route definitions
│       ├── screens/
│       │   ├── HomeScreen.kt       # Start screen
│       │   ├── PlayerSetupScreen.kt # Add player names
│       │   ├── DeckSelectionScreen.kt # Choose deck
│       │   └── GameScreen.kt       # Active gameplay
│       ├── theme/
│       │   └── ToucanTheme.kt      # Colors, typography, prompt colors
│       ├── utils/
│       │   └── Landscape.kt        # Orientation detection
│       └── viewmodel/
│           └── GameViewModel.kt    # Game state management
├── androidMain/                    # Android-specific code
└── iosMain/
    └── kotlin/.../MainViewController.kt  # iOS entry point
```

## Domain Models

### PromptType (Enum)
Categories that determine prompt appearance and behavior:
- `Normal` - Standard prompts (teal background)
- `Minigame` - Games and challenges (yellow-orange background)
- `Virus` - Persistent effects with cure (red background, blue for cure)
- `BottomsUp` - Finish your drink challenges (purple background)
- `Punishment` - Penalty prompts (dark orange background)
- `Ardente` - Spicy/adult challenges (hot pink background)

### Prompt (Sealed Interface)
All prompts implement:
```kotlin
sealed interface Prompt {
    val type: PromptType
    val numTargets: Int    // Number of %s placeholders for player names
    val text: String
}
```

Implementations:
- `NormalPrompt(text, numTargets)`
- `MinigamePrompt(text, numTargets)`
- `VirusPrompt(text, numTargets, secondary)` - includes cure text
- `BottomsUpPrompt(text, numTargets)`
- `PunishmentPrompt(text, numTargets)`
- `ArdentePrompt(text, numTargets)`

### PromptResult
The display-ready result after player name interpolation:
```kotlin
data class PromptResult(
    val text: String,           // Interpolated prompt text
    val type: PromptType,
    val isVirusCure: Boolean,   // True when showing virus cure
    val hasActiveVirus: Boolean // True if a virus effect is active
)
```

### PromptDeck
```kotlin
data class PromptDeck(
    val id: String,
    val name: String,
    val description: String,
    val prompts: List<Prompt>
)
```

## Game Engine

### Core Logic (`GameEngine.kt`)

**Initialization:**
1. Requires minimum 2 players
2. Filters prompts by player count (excludes prompts needing more targets than available players)
3. Shuffles eligible prompts and takes random subset (default: 30 prompts per game)

**Game Flow:**
1. `startGame()` - Returns first prompt
2. `nextPrompt()` - Returns next prompt or virus cure, null when game ends
3. `isGameOver()` - True when all prompts exhausted and no pending virus cure

**Virus Mechanics:**
- When a virus prompt appears, it becomes "active"
- Virus lasts 3-6 random turns
- After duration expires, cure prompt shown with same player targets
- Both primary and secondary text interpolated with same player names

**Player Interpolation:**
- `%s` placeholders replaced with random player names
- Same players used for virus primary and cure text

## Available Decks

Located in `composeResources/files/decks/`:

| Deck ID | Name | Description | Prompts |
|---------|------|-------------|---------|
| `getting_started` | Getting Started | Well-rounded party prompts | 100 |
| `bar_night` | Bar Night | Casual bar-friendly prompts | 97 |
| `getting_crazy` | Getting Crazy | Daring challenges | 67 |
| `ardente` | Ardente | Fiery/spicy challenges (18+) | 97 |

### JSON Format
```json
{
  "id": "deck_id",
  "name": "Display Name",
  "description": "Deck description",
  "prompts": [
    {
      "type": "normal",
      "numTargets": 2,
      "text": "%s, challenge %s to a thumb war"
    },
    {
      "type": "virus",
      "numTargets": 1,
      "text": "%s must speak in an accent",
      "secondary": "%s can speak normally again"
    }
  ]
}
```

## UI Screens

### HomeScreen
- App logo and title
- "Start Game" button
- Navigates to PlayerSetupScreen

### PlayerSetupScreen
- Text input to add player names
- List of added players with remove option
- Requires minimum 2 players
- "Continue" navigates to DeckSelectionScreen

### DeckSelectionScreen
- Lists available decks with name, description, prompt count
- Selecting a deck navigates to GameScreen

### GameScreen
States:
- **Loading** - Initializing game engine
- **Active** - Shows current prompt with tap-to-continue
- **GameOver** - Play again or exit options
- **Error** - Retry option

Active Game Features:
- Full-screen colored background based on prompt type
- Animated color transitions between prompts
- Prompt type label (top-left)
- Virus warning icon when virus active (top-right)
- Menu icon with Settings and Exit Game options (top-right)
- Safe area padding for notch/dynamic island

## Theme

### Prompt Type Colors
| Type | Background | Text |
|------|------------|------|
| Normal | Teal (#00BFA5) | White |
| Minigame | Yellow-orange (#FFAB00) | Black |
| Virus | Red (#E53935) | White |
| Virus Cure | Blue (#29B6F6) | Black |
| Bottoms Up | Purple (#7B1FA2) | White |
| Punishment | Dark orange (#E65100) | White |
| Ardente | Hot pink (#D81B60) | White |

### App Colors
- Primary: Toucan Orange (#FF6D00)
- Background Light: Cream (#FFF8E1)
- Background Dark: Black (#1A1A1A)
- Accent: Teal (#00BFA5)

## Navigation

Routes defined as serializable objects:
```kotlin
@Serializable object HomeRoute
@Serializable object PlayerSetupRoute
@Serializable data class DeckSelectionRoute(val playersJson: String)
@Serializable data class GameRoute(val deckId: String, val playersJson: String)
```

Flow: Home → PlayerSetup → DeckSelection → Game

## Configuration

### Game Settings (Configurable)
- `promptsPerGame`: Number of prompts per game session (default: 30)
- Virus duration: 3-6 turns (random)

### Future Settings (Planned)
- Prompts per game slider (15-50)
- Keep screen awake toggle
- Theme toggle (dark/light/system)
- Content type filters

## Platform-Specific

### iOS
- Entry point: `MainViewController()` returns `ComposeUIViewController`
- Safe area handling via `WindowInsets.safeDrawing`
- Framework name: `ToucanKMP`

### Android
- Standard Compose activity setup
- Uses `androidx.activity:activity-compose`

## Dependencies

Key libraries:
- Compose Multiplatform 1.9.x
- Kotlin 2.2.x
- Navigation Compose 2.9.x
- Kotlinx Serialization 1.7.x
- AndroidX Lifecycle ViewModel
