# 📱 Android Quiz App

A modern Android quiz application built with **Kotlin**, **Jetpack Compose**, and **MVVM architecture**. This app features 15 questions across 3 topics with a 30-second timer, immediate feedback, and comprehensive results.

---

## 🚀 Setup & Run

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK** 8 or higher
- **Android SDK** with API 26+ (minimum) and API 34 (target)

### Steps to Build and Run

#### On Emulator

1. **Open the project**
   - Launch Android Studio
   - Click `File → Open`
   - Navigate to and select the `QuizApp` folder
   - Click `OK`

2. **Wait for Gradle sync**
   - Android Studio will automatically sync Gradle dependencies
   - Wait for the process to complete (check bottom status bar)
   - If prompted, accept SDK licenses

3. **Create/Start an emulator**
   - Click `Tools → Device Manager`
   - If no emulator exists, click `Create Device`
   - Select a device (recommended: Pixel 5)
   - Select system image (recommended: API 34)
   - Click `Finish`
   - Start the emulator

4. **Run the app**
   - Click the green **Run** button (▶️) in the toolbar
   - Or press `Shift + F10`
   - Select your emulator from the device list
   - Click `OK`
   - The app will build and launch automatically

#### On Physical Device

1. **Enable Developer Options on your device**
   - Go to `Settings → About Phone`
   - Tap `Build Number` 7 times
   - Developer Options will be enabled

2. **Enable USB Debugging**
   - Go to `Settings → Developer Options`
   - Enable `USB Debugging`

3. **Connect your device**
   - Connect your Android device via USB cable
   - Accept the USB debugging prompt on your device

4. **Run the app**
   - Click the green **Run** button (▶️)
   - Select your device from the list
   - Click `OK`

#### Build APK (Optional)

```bash
# Navigate to project directory
cd QuizApp

# Build debug APK
./gradlew assembleDebug

# APK location: app/build/outputs/apk/debug/app-debug.apk
```

### Troubleshooting

- **Gradle sync failed**: Run `./gradlew clean` then rebuild
- **SDK not found**: Install Android SDK 34 via `Tools → SDK Manager`
- **App crashes**: Check Logcat for error messages
- **Emulator won't start**: Ensure virtualization is enabled in BIOS

---

## 🎯 Design Decisions

### 1. I chose **StateFlow over LiveData** because...

StateFlow is the modern, recommended approach for state management in Jetpack Compose. Unlike LiveData, StateFlow:
- Integrates seamlessly with Compose without needing `.observeAsState()`
- Provides better type safety with sealed classes
- Supports structured concurrency with coroutines natively
- Has a simpler API for complex state transformations
- Is lifecycle-aware by default when collected in Compose

**Implementation:**
```kotlin
private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()
```

### 2. I chose **Sealed Classes for UI States over Enums** because...

Sealed classes provide type-safe state representation with associated data, which enums cannot do. This approach:
- Allows each state to carry different data (Quiz state has timer, Result state has performance)
- Enables exhaustive `when` expressions that the compiler enforces
- Prevents invalid state combinations at compile time
- Makes state transitions explicit and traceable
- Improves code readability and maintainability

**Implementation:**
```kotlin
sealed class QuizUiState {
    object Loading : QuizUiState()
    data class Quiz(
        val questions: List<Question>,
        val currentQuestionIndex: Int,
        val score: Int,
        val timeRemaining: Int
        // ... more properties
    ) : QuizUiState()
    data class Result(
        val score: Int,
        val results: List<QuizResult>
        // ... more properties
    ) : QuizUiState()
}
```

### 3. I chose **SavedStateHandle over ViewModel only** because...

SavedStateHandle ensures state survives not just configuration changes (like rotation) but also process death. This provides:
- Seamless user experience during screen rotation
- State preservation when the app is killed by the system (low memory)
- Automatic state restoration without manual Bundle handling
- Integration with ViewModel lifecycle
- Better user experience (quiz progress isn't lost)

**Implementation:**
```kotlin
class QuizViewModel(
    private val repository: QuizRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    // Save state
    savedStateHandle[KEY_CURRENT_INDEX] = currentIndex
    
    // Restore state
    val savedIndex = savedStateHandle.get<Int>(KEY_CURRENT_INDEX) ?: 0
}
```

### 4. I chose **Repository Pattern over Direct Data Access** because...

The repository pattern abstracts the data source, making the code more flexible and testable:
- Easy to swap implementations (hardcoded → API → database)
- ViewModel doesn't need to know where data comes from
- Mockable for unit testing
- Single responsibility principle (separation of concerns)
- Future-proof for adding caching, offline support, or remote APIs

**Implementation:**
```kotlin
interface QuizRepository {
    fun getQuestions(): List<Question>
}

class QuizRepositoryImpl : QuizRepository {
    override fun getQuestions(): List<Question> {
        // Currently returns hardcoded questions
        // Can easily be replaced with API calls or database queries
    }
}
```

### 5. I chose **Jetpack Compose over XML Layouts** because...

Compose is the modern, declarative UI toolkit for Android that offers:
- Less boilerplate code (no findViewById, no XML)
- Reactive UI updates automatically when state changes
- Better performance with smart recomposition
- Easier animations with built-in animation APIs
- Type-safe UI construction
- Better integration with Kotlin features

**Example:**
```kotlin
@Composable
fun QuizScreen(state: QuizUiState.Quiz) {
    // UI automatically updates when state changes
    Text(text = "Score: ${state.score}")
}
```

### 6. I chose **MVVM Architecture over MVC/MVP** because...

MVVM is Google's recommended architecture for Android apps and provides:
- Clear separation between UI and business logic
- ViewModel survives configuration changes automatically
- Easier testing (can test ViewModel without UI)
- Unidirectional data flow (predictable state changes)
- Better suited for reactive programming with StateFlow

**Architecture:**
```
View (Compose) → ViewModel → Repository → Data Source
     ↑              ↓
     └── StateFlow ─┘
```

---

## 📚 New to Kotlin

### What Was Unfamiliar

#### 1. **Sealed Classes**

Coming from Java, sealed classes were a new concept. They restrict class hierarchies to a fixed set of types.

**How I learned:**
- Read Kotlin documentation on sealed classes
- Studied examples in Android architecture samples
- Practiced by implementing `QuizUiState` as a sealed class

**Resources:**
- [Kotlin Docs: Sealed Classes](https://kotlinlang.org/docs/sealed-classes.html)
- Android Developer guides on state management

**What I learned:**
Sealed classes are perfect for representing UI states because they ensure all possible states are handled at compile time.

#### 2. **Coroutines and Flow**

Kotlin's approach to asynchronous programming was different from Java's threads and callbacks.

**How I learned:**
- Started with basic coroutine concepts (launch, async, suspend)
- Learned about Flow as a reactive stream
- Practiced with StateFlow for state management
- Implemented the timer using `viewModelScope.launch` and `delay()`

**Resources:**
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Android Developers: Kotlin Flow](https://developer.android.com/kotlin/flow)
- YouTube tutorials on coroutines

**What I learned:**
Coroutines make asynchronous code look synchronous and are much easier to read than callbacks. `viewModelScope` automatically cancels coroutines when the ViewModel is cleared.

#### 3. **Extension Properties**

The ability to add computed properties to existing classes without inheritance was new.

**How I learned:**
- Read Kotlin documentation on extensions
- Saw examples in the codebase
- Implemented custom extensions like `val QuizUiState.Quiz.progress`

**Resources:**
- [Kotlin Docs: Extensions](https://kotlinlang.org/docs/extensions.html)

**Example:**
```kotlin
val QuizUiState.Quiz.progress: Float
    get() = (currentQuestionIndex + 1).toFloat() / questions.size
```

**What I learned:**
Extensions keep code clean by adding functionality close to where it's used without modifying the original class.

#### 4. **Data Classes**

Kotlin's data classes automatically generate `equals()`, `hashCode()`, `toString()`, and `copy()`.

**How I learned:**
- Read Kotlin basics documentation
- Used data classes for all models (`Question`, `QuizResult`)
- Leveraged the `copy()` function for immutable state updates

**Resources:**
- [Kotlin Docs: Data Classes](https://kotlinlang.org/docs/data-classes.html)

**What I learned:**
Data classes reduce boilerplate significantly and the `copy()` function is perfect for updating immutable state.

#### 5. **Jetpack Compose**

Declarative UI was a paradigm shift from imperative XML layouts.

**How I learned:**
- Followed Android Compose tutorials
- Read Compose documentation
- Studied state management in Compose
- Built composables incrementally (started with simple ones)

**Resources:**
- [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
- [Compose Pathway](https://developer.android.com/courses/pathways/compose)
- Android Compose samples on GitHub

**What I learned:**
Compose makes UI code more readable and maintainable. The concept of recomposition and state hoisting took time to understand but makes sense now.

#### 6. **Higher-Order Functions and Lambdas**

Kotlin's functional programming features were more powerful than Java's.

**How I learned:**
- Practiced with simple examples
- Used lambdas for callbacks (`onAnswerSelected: (Int) -> Unit`)
- Learned about trailing lambda syntax

**Resources:**
- [Kotlin Docs: Higher-Order Functions](https://kotlinlang.org/docs/lambdas.html)

**What I learned:**
Lambdas make code more concise and readable, especially for callbacks in Compose.

### Learning Approach

1. **Read documentation first** - Understood concepts before coding
2. **Study examples** - Looked at official Android samples
3. **Incremental implementation** - Built features one at a time
4. **Experimentation** - Tried different approaches to understand trade-offs
5. **Code review** - Reviewed my own code to improve structure

### Key Resources Used

- **Official Kotlin Documentation** - Primary reference
- **Android Developers Website** - Architecture guides and best practices
- **Jetpack Compose Documentation** - UI implementation
- **Stack Overflow** - Specific problem-solving
- **GitHub Android Samples** - Real-world examples
- **YouTube Tutorials** - Visual learning for complex concepts

---

## 🐛 Known Limitations

### 1. **No Persistence**

**Current:** Quiz history and user progress are not saved. When the app is closed, all data is lost.

**What I would fix:**
- Implement Room database to store quiz history
- Save user's best scores per topic
- Track statistics (total quizzes taken, average score, etc.)
- Add a history screen showing past quiz results

**Why it matters:** Users can't track their progress over time or review previous attempts.

### 2. **Hardcoded Questions**

**Current:** All 15 questions are hardcoded in `QuizRepository.kt`. No dynamic content.

**What I would fix:**
- Integrate with a REST API to fetch questions dynamically
- Implement caching for offline support
- Add ability to download new question packs
- Support different difficulty levels from the backend

**Why it matters:** Limited content variety and no way to update questions without app updates.

### 3. **No Question Randomization Within Options**

**Current:** Answer options (A, B, C, D) are in a fixed order. Users might memorize positions.

**What I would fix:**
- Shuffle the options array for each question
- Track the correct answer by content, not index
- Ensure the correct answer moves to different positions

**Why it matters:** Reduces the effectiveness of the quiz as a learning tool.

### 4. **Timer Precision**

**Current:** Uses `delay(1000)` which can drift slightly over time due to coroutine scheduling.

**What I would fix:**
- Use `System.currentTimeMillis()` for accurate time tracking
- Calculate remaining time based on start time vs current time
- Ensure timer is accurate even under heavy load

**Why it matters:** Timer might not be exactly 30 seconds, affecting fairness.

### 5. **No Accessibility Testing**

**Current:** App hasn't been tested with TalkBack or other accessibility services.

**What I would fix:**
- Add content descriptions to all interactive elements
- Test with TalkBack screen reader
- Ensure proper focus order
- Add semantic properties for better accessibility
- Test with large font sizes

**Why it matters:** App may not be usable for users with disabilities.

### 6. **No Unit or UI Tests**

**Current:** No automated tests exist for the codebase.

**What I would fix:**
- Add JUnit tests for ViewModel logic
- Test state transitions and edge cases
- Add Compose UI tests for screens
- Test timer behavior
- Mock repository for testing

**Example tests:**
```kotlin
@Test
fun `selectAnswer updates score correctly`() {
    // Test ViewModel logic
}

@Test
fun `quizScreen displays question`() {
    // Test UI rendering
}
```

**Why it matters:** No automated verification of functionality, making refactoring risky.

### 7. **No Multi-language Support**

**Current:** All text is hardcoded in English. No internationalization (i18n).

**What I would fix:**
- Move all strings to `strings.xml`
- Add translations for multiple languages
- Support RTL (Right-to-Left) languages
- Test with different locales

**Why it matters:** Limits the app to English-speaking users only.

### 8. **No User Profiles or Authentication**

**Current:** No way to identify individual users or sync data across devices.

**What I would fix:**
- Implement Firebase Authentication
- Add user profiles with avatars
- Sync quiz history to the cloud
- Add leaderboards and social features

**Why it matters:** Can't provide personalized experiences or competitive features.

### 9. **Limited Error Handling**

**Current:** Basic error handling, no user-friendly error messages.

**What I would fix:**
- Add try-catch blocks for potential failures
- Show user-friendly error messages
- Implement retry mechanisms
- Add error logging (Firebase Crashlytics)

**Why it matters:** App might crash or behave unexpectedly in edge cases.

### 10. **No Analytics**

**Current:** No tracking of user behavior or app performance.

**What I would fix:**
- Integrate Firebase Analytics
- Track quiz completion rates
- Monitor which questions are most difficult
- Track app performance metrics
- Use data to improve the app

**Why it matters:** Can't make data-driven decisions for improvements.

### 11. **No Pause/Resume Functionality**

**Current:** Once a quiz starts, it can't be paused. Timer keeps running even if user leaves the app.

**What I would fix:**
- Add pause button
- Pause timer when app goes to background
- Resume from where user left off
- Save partial progress

**Why it matters:** Users might be interrupted and lose their progress.

### 12. **No Question Categories or Filters**

**Current:** All questions are mixed. Users can't choose specific topics.

**What I would fix:**
- Add topic selection screen
- Allow users to choose which topics to include
- Add difficulty filters
- Support custom quiz creation

**Why it matters:** Users can't focus on specific areas they want to practice.

---

## 🎯 Features Implemented

### Core Features

- ✅ 15 questions across 3 topics (Kotlin, Android, Jetpack Compose)
- ✅ 30-second timer per question
- ✅ Immediate feedback (green for correct, red for incorrect)
- ✅ Auto-advance to next question
- ✅ Score tracking
- ✅ Comprehensive results screen
- ✅ Topic-wise performance breakdown
- ✅ Retry functionality

### Technical Features

- ✅ MVVM architecture with clean separation
- ✅ StateFlow for reactive state management
- ✅ SavedStateHandle for configuration change survival
- ✅ Navigation Compose for screen navigation
- ✅ Material Design 3 with dark mode support
- ✅ Smooth animations using Compose animation APIs
- ✅ Repository pattern for data abstraction

---

## 📁 Project Structure

```
QuizApp/
├── app/src/main/java/com/quizapp/
│   ├── data/
│   │   └── QuizRepository.kt          # Repository with 15 questions
│   ├── domain/
│   │   ├── Question.kt                # Question data model
│   │   └── QuizResult.kt              # Result models
│   ├── presentation/
│   │   ├── QuizViewModel.kt           # State management
│   │   ├── QuizUiState.kt             # Sealed UI states
│   │   ├── HomeScreen.kt              # Home/Welcome screen
│   │   ├── QuizScreen.kt              # Quiz UI
│   │   ├── ResultScreen.kt            # Results UI
│   │   └── QuizViewModelFactory.kt    # ViewModel factory
│   ├── navigation/
│   │   └── NavGraph.kt                # Navigation setup
│   ├── ui/theme/
│   │   ├── Color.kt                   # Color definitions
│   │   ├── Theme.kt                   # Theme configuration
│   │   └── Type.kt                    # Typography
│   └── MainActivity.kt                # Entry point
└── [Gradle files and resources]
```

---

## 🛠️ Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| Kotlin | 1.9.0 | Programming language |
| Jetpack Compose | 2024.02.00 | UI framework |
| Material 3 | Latest | Design system |
| Navigation Compose | 2.7.7 | Screen navigation |
| ViewModel | 2.7.0 | State management |
| StateFlow | 1.7.3 | Reactive state |
| Coroutines | 1.7.3 | Async operations |

---

## 📄 License

This project is created for educational purposes as part of an Android development assignment.

---

## 👨‍💻 Author

Built with modern Android development best practices, demonstrating:
- Clean architecture
- MVVM pattern
- Jetpack Compose
- State management
- Material Design 3

---

**Happy Coding! 🚀**
