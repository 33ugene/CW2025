# Tetris Game - Coursework Documentation

## GitHub

**Repository Link:** [Add your GitHub repository URL here]

---

## Compilation Instructions

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Apache Maven 3.6+ (or use the included Maven wrapper `mvnw`)

### Step-by-Step Compilation

1. **Navigate to the project directory:**
   ```bash
   cd /path/to/CW2025
   ```

2. **Build the project using Maven:**
   ```bash
   ./mvnw clean package
   ```
   Or on Windows:
   ```bash
   mvnw.cmd clean package
   ```

3. **Run the application:**
   
   **Option A: Using the launcher script (Recommended)**
   ```bash
   ./run.sh
   ```
   Or on Windows:
   ```bash
   run.bat
   ```
   
   **Option B: Using Maven directly**
   ```bash
   ./mvnw javafx:run
   ```

### Alternative: Build Executable JAR

To create a standalone JAR file:
```bash
./build.sh
```

**Note:** The JAR file requires JavaFX modules to be available. Use `./run.sh` for the most reliable execution method.

### Dependencies
All dependencies are managed by Maven and will be automatically downloaded during the build process:
- JavaFX Controls 21.0.6
- JavaFX FXML 21.0.6
- JUnit 5.12.1 (for testing)

---

## Implemented and Working Properly

### 1. Main Menu System
- **Location:** `src/main/java/com/comp2042/MainMenuController.java`, `src/main/resources/mainMenu.fxml`
- **Description:** A fully functional main menu screen that appears before the game starts. Includes theme selection, high score display, and start/exit buttons.
- **Status:** ✅ Fully functional

### 2. Theme System
- **Location:** `src/main/java/com/comp2042/ThemeManager.java`
- **Description:** Comprehensive theming system with 4 preset themes (Dark Mode, Neon, Pastel, Light Mode). Themes dynamically change colors for all UI elements including background, cards, text, buttons, and brick colors.
- **Status:** ✅ Fully functional

### 3. Hold Feature
- **Location:** `src/main/java/com/comp2042/HoldManager.java`, integrated in `GameController.java`
- **Description:** Players can press 'C' to hold the current piece and swap it with a previously held piece. The hold display shows the held piece and indicates when hold is available.
- **Status:** ✅ Fully functional

### 4. Next Piece Preview (3 Pieces)
- **Location:** `GuiController.java` - `renderNextThreePreview()` method
- **Description:** Displays the next 3 upcoming pieces in the hold panel, giving players better strategic planning.
- **Status:** ✅ Fully functional

### 5. High Score System with Persistence
- **Location:** `src/main/java/com/comp2042/HighScoreManager.java`
- **Description:** Tracks and saves high scores to `highscore.dat` file. High scores persist across game sessions and are displayed in both the main menu and in-game stats panel.
- **Status:** ✅ Fully functional

### 6. Pause Menu with Return to Main Menu
- **Location:** `GuiController.java` - `handleReturnToMenu()` method, `gameLayout.fxml`
- **Description:** When paused (P key), players can resume the game or return to the main menu. The pause overlay is styled according to the current theme.
- **Status:** ✅ Fully functional

### 7. Line Clear Animations
- **Location:** `GuiController.java` - `animateLineClear()` method
- **Description:** When lines are cleared, animated effects include white flash, glow effect, fade-out, and scale-up animations. The animation completes before the board refreshes.
- **Status:** ✅ Fully functional

### 8. Sound Effects System
- **Location:** `src/main/java/com/comp2042/SoundManager.java`
- **Description:** Comprehensive sound effects for all game actions:
  - Line clears (different pitches for 1-4 lines, with special Tetris sound)
  - Movement (left/right)
  - Rotation
  - Soft drop and hard drop
  - Hold/swap
  - Piece lock
  - Level up
  - Game over
- **Status:** ✅ Fully functional

### 9. Enhanced UI/UX
- **Location:** `GuiController.java`, `gameLayout.fxml`, `mainMenu.fxml`
- **Description:** 
  - Improved board alignment and centering
  - Better visual styling with rounded corners, borders, and shadows
  - Theme-aware UI elements
  - Improved text contrast for light themes
  - Better organized sidebar layout
- **Status:** ✅ Fully functional

### 10. Timer System
- **Location:** `src/main/java/com/comp2042/TimerManager.java`
- **Description:** Tracks game time and displays it in MM:SS format. Timer pauses when game is paused and stops on game over.
- **Status:** ✅ Fully functional

### 11. Score System with Level Progression
- **Location:** `src/main/java/com/comp2042/ScoreManager.java`
- **Description:** Standard Tetris scoring system with level progression. Points awarded for line clears (100/300/500/800 for 1/2/3/4 lines), soft drops, and hard drops. Level increases every 10 lines cleared.
- **Status:** ✅ Fully functional

### 12. Ghost Piece Preview
- **Location:** `GuiController.java` - `drawGhostPiece()` method
- **Description:** Shows a semi-transparent preview of where the current piece will land, helping players plan placements.
- **Status:** ✅ Fully functional

---

## Implemented but Not Working Properly

### None
All implemented features are working as expected. No known issues or bugs.

---

## Features Not Implemented

### 1. Multiplayer Mode
- **Reason:** Not specified in requirements. Would require significant networking infrastructure and additional UI components.

### 2. Replay/Recording System
- **Reason:** Not specified in requirements. Would require implementing a move recording system and playback mechanism.

### 3. Custom Key Bindings
- **Reason:** Not specified in requirements. Current key bindings are hardcoded but functional.

### 4. Leaderboard with Player Names
- **Reason:** High score system stores scores but doesn't include player name input. This would require additional UI for name entry.

### 5. Game Statistics Tracking
- **Reason:** Basic stats (score, lines, level, time) are tracked, but detailed statistics like average lines per game, play count, etc. are not implemented.

---

## New Java Classes

### 1. `MainMenuController.java`
- **Location:** `src/main/java/com/comp2042/MainMenuController.java`
- **Purpose:** Controls the main menu screen. Handles theme selection, game start, exit, and high score display. Manages scene transitions between menu and game.
- **Key Methods:**
  - `handleStartGame()`: Loads game scene and initializes game controller
  - `handleExit()`: Exits the application
  - `applyThemeStyles()`: Applies selected theme to menu UI elements

### 2. `ThemeManager.java`
- **Location:** `src/main/java/com/comp2042/ThemeManager.java`
- **Purpose:** Manages game themes and color palettes. Provides dynamic styling for all UI components based on selected theme (Dark, Neon, Pastel, Light).
- **Key Features:**
  - `ThemePalette` inner class: Encapsulates all styling properties for a theme
  - `getCurrentPalette()`: Returns current theme's color palette
  - `setTheme()`: Changes the active theme
  - Theme-specific brick colors and UI styles

### 3. `HighScoreManager.java`
- **Location:** `src/main/java/com/comp2042/HighScoreManager.java`
- **Purpose:** Manages high score persistence. Saves high scores to `highscore.dat` file and loads them on startup. Tracks top 10 scores.
- **Key Methods:**
  - `addHighScore(int score)`: Adds a new score and saves to file
  - `getHighScore()`: Returns the highest score
  - `getHighScores()`: Returns list of all high scores
  - `loadHighScores()`: Loads scores from file on initialization
  - `saveHighScores()`: Persists scores to file

### 4. `HoldManager.java`
- **Location:** `src/main/java/com/comp2042/HoldManager.java`
- **Purpose:** Manages the hold/swap feature. Tracks held piece and enforces hold rules (can only hold once per piece placement).
- **Key Methods:**
  - `holdBrick(Brick brick)`: Attempts to hold a brick, returns result
  - `canHold()`: Checks if hold is available
  - `getHeldBrick()`: Returns currently held brick
  - `resetHold()`: Resets hold availability after piece locks

### 5. `SoundManager.java`
- **Location:** `src/main/java/com/comp2042/SoundManager.java`
- **Purpose:** Manages all game sound effects. Provides procedural beep sounds for different game events using Java's Toolkit.
- **Key Methods:**
  - `playSound(String soundName)`: Plays a specific sound effect
  - `playLineClear(int lines)`: Plays appropriate sound for line clear (1-4 lines)
  - Different sound frequencies and durations for various game events

### 6. `TimerManager.java`
- **Location:** `src/main/java/com/comp2042/TimerManager.java`
- **Purpose:** Tracks game time. Provides formatted time display and pause/resume functionality.
- **Key Methods:**
  - `start()`: Starts the timer
  - `pause()`: Pauses the timer
  - `reset()`: Resets timer to zero
  - `getFormattedTime()`: Returns time as "MM:SS" string

### 7. `ScoreManager.java`
- **Location:** `src/main/java/com/comp2042/ScoreManager.java`
- **Purpose:** Manages game scoring and level progression. Implements standard Tetris scoring system.
- **Key Methods:**
  - `addLinesCleared(int lines)`: Adds points for cleared lines and updates level
  - `addSoftDropPoints()`: Adds points for soft drop
  - `addHardDropPoints(int distance)`: Adds points for hard drop
  - `getLevel()`: Returns current level
  - `reset()`: Resets score, lines, and level for new game

---

## Modified Java Classes

### 1. `Main.java`
- **Location:** `src/main/java/com/comp2042/Main.java`
- **Changes Made:**
  - Modified `start()` method to load `mainMenu.fxml` instead of `gameLayout.fxml`
  - Added `ThemeManager` initialization
  - Passes `primaryStage` and `themeManager` to `MainMenuController`
- **Rationale:** Changed entry point to show main menu first, allowing theme selection before starting the game.

### 2. `GuiController.java`
- **Location:** `src/main/java/com/comp2042/GuiController.java`
- **Changes Made:**
  - Added `HIDDEN_ROWS` constant (value: 2) to correctly offset rendering for hidden spawn rows
  - Added `brickPanel` configuration (`managed=false`, `mouseTransparent=true`) to allow floating above game panel
  - Implemented `updateActiveBrickPosition()` method using `getCellWidth()` and `getCellHeight()` for accurate positioning
  - Added `setPrimaryStage()` and `setThemeManager()` methods
  - Implemented `applyTheme()` method to dynamically style UI elements based on theme
  - Added `animateLineClear()` method for line clear animations
  - Added `updateHighScore()` method to display high score in stats panel
  - Modified `refreshGameBackground()` to account for hidden rows
  - Added pause menu handlers: `handleResumeGame()` and `handleReturnToMenu()`
  - Added `renderNextThreePreview()` to display next 3 pieces
  - Updated `getFillColor()` to retrieve colors from `ThemeManager`
  - Added theme-aware styling for all UI components
- **Rationale:** 
  - Fixed board alignment and brick positioning issues
  - Integrated theme system throughout the UI
  - Added visual feedback (animations) and improved user experience
  - Added navigation between game and main menu

### 3. `GameController.java`
- **Location:** `src/main/java/com/comp2042/GameController.java`
- **Changes Made:**
  - Added `HoldManager`, `HighScoreManager`, and `SoundManager` instances
  - Integrated hold feature in `onHoldEvent()` method
  - Added sound effect calls throughout game events (move, rotate, drop, lock, line clear, level up, game over)
  - Modified line clear handling to trigger animations via `viewGuiController.animateLineClear()`
  - Added high score checking and updating on game over
  - Modified `updateNextPiecePreview()` to fetch 3 bricks instead of 1
  - Added `holdManager.resetHold()` calls after piece locks
- **Rationale:** 
  - Integrated new features (hold, sounds, animations, high scores) into game flow
  - Enhanced user feedback with sound effects
  - Improved game experience with visual and audio feedback

### 4. `ClearRow.java`
- **Location:** `src/main/java/com/comp2042/ClearRow.java`
- **Changes Made:**
  - Added `clearedRowIndices` field to track which specific rows were cleared
  - Added constructor overload that accepts `List<Integer> clearedRowIndices`
  - Added `getClearedRowIndices()` method
- **Rationale:** Needed to know which rows were cleared to animate them specifically in the UI.

### 5. `MatrixOperations.java`
- **Location:** `src/main/java/com/comp2042/MatrixOperations.java`
- **Changes Made:**
  - Modified `checkRemoving()` method to track cleared row indices
  - Updated return statement to pass `clearedRows` list to `ClearRow` constructor
- **Rationale:** Required to provide row indices to `ClearRow` for animation purposes.

### 6. `SimpleBoard.java`
- **Location:** `src/main/java/com/comp2042/SimpleBoard.java`
- **Changes Made:**
  - Added `getCurrentBrick()` method to retrieve the active brick
- **Rationale:** Needed by `GameController` to access the current brick for the hold feature.

---

## Unexpected Problems

### 1. JavaFX Module System Issues
- **Problem:** Initially attempted to create a fat JAR with all dependencies using Maven Shade Plugin. However, JavaFX modules cannot be easily bundled into a single JAR due to module system requirements.
- **Solution:** Created launcher scripts (`run.sh`, `run.bat`) that use Maven JavaFX plugin directly, which properly handles JavaFX module path. Also documented that standalone JAR requires JavaFX modules to be available separately.
- **Location:** `pom.xml`, `run.sh`, `build.sh`

### 2. Board Alignment and Brick Positioning
- **Problem:** Initial implementation had misaligned bricks, floating blocks, and incorrect board borders. The `brickPanel` was interfering with layout management, and hidden rows were not properly accounted for.
- **Solution:** 
  - Introduced `HIDDEN_ROWS` constant to offset rendering
  - Set `brickPanel` to `managed=false` and `mouseTransparent=true` to allow floating
  - Refactored positioning logic to use `getCellWidth()` and `getCellHeight()` methods
  - Wrapped game panel and brick panel in a `StackPane` with proper clipping
- **Location:** `GuiController.java`, `gameLayout.fxml`

### 3. FXML Separator Import Error
- **Problem:** Added `<Separator>` element to `mainMenu.fxml` but forgot to import it, causing `LoadException`.
- **Solution:** Added `<?import javafx.scene.control.Separator?>` to FXML imports.
- **Location:** `src/main/resources/mainMenu.fxml`

### 4. Theme Text Visibility in Light Modes
- **Problem:** Score labels and headers were not visible in Light and Pastel themes because they had hard-coded white text colors that didn't adapt to themes.
- **Solution:** Removed hard-coded text colors from FXML and implemented dynamic color application through `ThemeManager`. Updated `ThemeManager` to provide darker text colors for light backgrounds.
- **Location:** `gameLayout.fxml`, `ThemeManager.java`, `GuiController.java`

### 5. Sound System Limitations
- **Problem:** Initially attempted to create WAV files programmatically for sound effects, but this was complex and platform-dependent.
- **Solution:** Simplified to use Java's `Toolkit.beep()` method with different frequencies and durations. This provides cross-platform sound support without external dependencies.
- **Location:** `SoundManager.java`

### 6. Line Clear Animation Timing
- **Problem:** Board was refreshing immediately after line clear, cutting off animations.
- **Solution:** Modified `animateLineClear()` to accept a callback function. Game controller now passes a callback that refreshes the board only after animation completes.
- **Location:** `GuiController.java`, `GameController.java`

### 7. High Score File Location
- **Problem:** High score file was being created in project root, which might not be writable in all deployment scenarios.
- **Solution:** Current implementation saves to project root (same directory as JAR). For production, this could be improved to use user's home directory, but current solution works for coursework requirements.
- **Location:** `HighScoreManager.java`

---

## Additional Notes

### File Structure
- **Source Code:** `src/main/java/com/comp2042/`
- **FXML Layouts:** `src/main/resources/`
- **Build Scripts:** `build.sh`, `build-native.sh`, `run.sh`, `run.bat`
- **Configuration:** `pom.xml`

### Key Design Decisions
1. **Theme System:** Implemented as a centralized `ThemeManager` with `ThemePalette` objects to ensure consistent theming across all UI components.
2. **Hold Feature:** Implemented with a state machine pattern in `HoldManager` to enforce game rules (one hold per piece placement).
3. **Animation System:** Used JavaFX `ParallelTransition` and `FadeTransition` for smooth line clear animations.
4. **Sound System:** Simplified to use platform-native beep sounds for maximum compatibility.

### Testing
The game has been tested on:
- macOS (primary development platform)
- Java 17+ runtime
- Various theme combinations
- All game features and edge cases

---

## Conclusion

This Tetris implementation includes all core gameplay features plus significant enhancements:
- Complete theming system
- Hold/swap functionality
- Visual and audio feedback
- Persistent high scores
- Enhanced UI/UX
- Main menu system

All features are fully functional and integrated into a cohesive gaming experience.


