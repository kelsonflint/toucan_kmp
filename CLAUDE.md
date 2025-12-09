Below is a **clean, complete, professional context file** summarizing the entire Toucan project, including domain, data, UI, KMP structure, and architectural decisions.
You can drop this into your repo as `CONTEXT.md`.

---

# **TOUCAN — Project Context & Architecture Overview**

*Last Updated: 2025-11*

---

## **1. Project Summary**

**Toucan** is a drinking-game style mobile app built using **Kotlin Multiplatform (KMP)**.
The game is played by a group of players who pass the device around, taking turns revealing prompts. Each prompt contains instructions, minigames, or special effects such as “viruses” that modify the gameplay for multiple turns.

The app consists of:

* A **shared Kotlin Multiplatform domain layer**
* An **Android Jetpack Compose UI**
* An **iOS SwiftUI UI** consuming the shared framework
* A simple but scalable **game engine** that manages turns, decks, prompt interpolation, and special rules

The goal is to rewrite an old iOS-only project into a fully KMP-shared game with room for expansion.

---

# **2. Core Domain Concepts**

### **2.1 Player**

A simple string name.

### **2.2 Prompt**

A sealed hierarchy describing game prompts.
Prompts interpolate player names based on `numTargets`.

Types supported:

* `NormalPrompt(text, numTargets)`
* `MinigamePrompt(text, numTargets)`
* `VirusPrompt(text, secondary)`

All implement:

```kotlin
sealed interface Prompt {
    val type: PromptType
    val numTargets: Int
}
```

### **2.3 PromptType**

```kotlin
enum class PromptType { Normal, Minigame, Virus }
```

### **2.4 PromptJson Format**

Used for loading prompts from bundled JSON.

Example:

```json
{
  "type": "normal",
  "numTargets": 2,
  "text": "%s, kiss %s's butt or drink 2 times"
}
```

Virus example:

```json
{
  "type": "virus",
  "text": "Time to spin the bottle!",
  "secondary": "Spin the bottle game is over!"
}
```

### **2.5 PromptDeck**

Deck metadata + parsed prompt list:

```kotlin
data class PromptDeck(
    val id: String,
    val name: String,
    val description: String,
    val prompts: List<Prompt>
)
```

### **2.6 GameEngine**

Responsible for:

* Randomizing turns
* Drawing prompts
* Interpolating player names
* Handling virus state (active virus + curing prompt)
* Determining when the game ends

API:

```kotlin
class GameEngine(
    deck: PromptDeck,
    players: List<String>
) {
    fun startGame(): PromptResult
    fun nextTurn(): PromptResult?
}
```

`PromptResult` includes `.text` and other metadata if required later.

---

# **3. KMP Layer Structure**

```
shared/
  ├─ src/
  │   ├─ commonMain/
  │   │    ├─ domain/
  │   │    │    ├─ models/
  │   │    │    ├─ engine/
  │   │    ├─ data/
  │   │    │    ├─ DeckRepository.kt
  │   │    ├─ util/
  │   ├─ androidMain/
  │   │    └─ json loading via context.assets
  │   ├─ iosMain/
  │        └─ json loading via NSBundle
  ├─ build.gradle.kts
```

### **3.1 DeckRepository**

Loads JSON decks via an `expect/actual` loader:

```kotlin
expect suspend fun loadDeckJson(deckId: String): DeckJson
```

`androidMain` loads from assets
`iosMain` loads from the app bundle

---

# **4. Android App Architecture (Jetpack Compose)**

### **4.1 Screens**

* **HomeScreen** — start, navigation entry point
* **PlayerSetupScreen** — input list of names
* **DeckSelectionScreen** — choose which deck to play
* **GameScreen** — runs the game using `GameViewModel`

### **4.2 Compose Navigation**

A simple NavGraph:

```
home → playerSetup → deckSelection → game
```

Arguments passed:

* `players` (JSON encoded list)
* `deckId`

### **4.3 GameViewModel (Simplified Architecture)**

Uses **no constructor args**.
Initialization happens via:

```kotlin
fun initialize(deckId: String, players: List<String>)
```

Called from `LaunchedEffect` inside the Game screen.

Flow-based UI state:

```kotlin
sealed class GameUiState {
    object Loading
    data class Active(val currentPrompt: String) : GameUiState()
    object GameOver : GameUiState()
}
```

This avoids complexity and supports recomposition safely.

---

# **5. iOS Project Architecture (SwiftUI)**

If possible, everything should be managed in Kotlin including UI and state management

---

# **6. Bundled JSON Decks**

Placed in:

### **Android:**

```
shared/src/androidMain/assets/decks/
```

### **iOS:**

```
iOSApp/Resources/decks/
```

Example file:

```
classic.json
spicy.json
minigames.json
```

Each contains:

```json
{
  "id": "classic",
  "name": "Classic Deck",
  "description": "A well-rounded set of prompts.",
  "prompts": [ ]
}
```

---

# **7. UI Themes**

### **ToucanTheme (Android)**

Green background:

```
#9ad494
```

Custom typography + rounded corners.

### **ToucanTheme (iOS)**

Mirrors Android palette and spacing.

---

# **8. High-Level Workflow**

1. User opens the app
2. Adds players
3. Selects a deck
4. GameViewModel initializes the engine
5. Game begins:

    * Turn advances
    * Prompt selected
    * Players interpolated
6. Viruses create secondary prompts that will end the virus a few turns later
7. Game ends when deck is exhausted
8. Show GameOver screen

---

# **9. Long-Term Extension Points**

* More prompt types (penalties, vote prompts, timed prompts)
* In-app deck marketplace
* Online multiplayer
* “Creator mode” for custom decks
* Analytics for deck usage
* Animation and haptic feedback for prompt reveals

---

# **10. Project Philosophy**

* Make **shared code** own all core logic
* Make the app feel **native** on each platform
* Keep UI structure **simple and modular**
* Optimize for **velocity**, not premature complexity
* Allow the domain to grow without UI rewrites

---

If you'd like, I can also generate:

📌 A *diagram version* of this context
📌 A *setup checklist for a new contributor*
📌 A *pitch deck style summary*
📌 A *UML diagram for the Prompt/Engine relationship*

Just tell me!
