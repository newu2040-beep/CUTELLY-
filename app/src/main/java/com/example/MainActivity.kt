package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AnimationsScreen
import com.example.ui.screens.GesturesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.StickerCreatorScreen
import com.example.ui.screens.StickersScreen
import com.example.ui.screens.ThemesScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.CutellyBackground
import com.example.ui.theme.CutellyBlush
import com.example.ui.theme.CutellyInk
import com.example.ui.theme.CutellySecondaryText
import com.example.ui.theme.CutellySoftPink
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CutellyViewModel

enum class Screen {
    WELCOME,
    HOME,
    STICKERS,
    GESTURES,
    ANIMATIONS,
    PROFILE,
    THEMES,
    STICKER_CREATOR
}

data class NavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class MainActivity : ComponentActivity() {

    private val viewModel: CutellyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()
            val currentTheme by viewModel.currentTheme.collectAsState()
            val themeMode by viewModel.themeMode.collectAsState()
            val uiDensityMode by viewModel.uiDensityMode.collectAsState()

            var currentScreen by remember {
                mutableStateOf(if (isOnboardingCompleted) Screen.HOME else Screen.WELCOME)
            }

            // Handle incoming intents (e.g. from floating overlay mini-menu)
            LaunchedEffect(intent) {
                handleIntentNavigation(intent) { targetScreen ->
                    currentScreen = targetScreen
                }
            }

            MyApplicationTheme(
                themeMode = themeMode,
                activeTheme = currentTheme,
                uiDensityMode = uiDensityMode
            ) {
                CutellyAppScaffold(
                    viewModel = viewModel,
                    currentScreen = currentScreen,
                    onNavigate = { newScreen -> currentScreen = newScreen }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkOverlayServiceState()
    }

    private fun handleIntentNavigation(intent: Intent?, onNavigate: (Screen) -> Unit) {
        when (intent?.getStringExtra("navigate_to")) {
            "stickers" -> onNavigate(Screen.STICKERS)
            "gestures" -> onNavigate(Screen.GESTURES)
            "animations" -> onNavigate(Screen.ANIMATIONS)
            "themes" -> onNavigate(Screen.THEMES)
            "profile" -> onNavigate(Screen.PROFILE)
            "home" -> onNavigate(Screen.HOME)
        }
    }
}

@Composable
fun CutellyAppScaffold(
    viewModel: CutellyViewModel,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val navItems = listOf(
        NavItem(Screen.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
        NavItem(Screen.STICKERS, "Stickers", Icons.Filled.Palette, Icons.Outlined.Palette),
        NavItem(Screen.GESTURES, "Gestures", Icons.Filled.TouchApp, Icons.Outlined.TouchApp),
        NavItem(Screen.PROFILE, "Profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    // Back handling
    BackHandler(enabled = currentScreen != Screen.HOME && currentScreen != Screen.WELCOME) {
        onNavigate(Screen.HOME)
    }

    val showBottomBar = currentScreen in listOf(Screen.HOME, Screen.STICKERS, Screen.GESTURES, Screen.PROFILE, Screen.THEMES)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    navItems.forEach { item ->
                        val isSelected = when (item.screen) {
                            Screen.HOME -> currentScreen == Screen.HOME
                            Screen.STICKERS -> currentScreen == Screen.STICKERS
                            Screen.GESTURES -> currentScreen == Screen.GESTURES
                            Screen.PROFILE -> currentScreen in listOf(Screen.PROFILE, Screen.THEMES)
                            else -> false
                        }
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { onNavigate(item.screen) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Crossfade(targetState = currentScreen, label = "screen_crossfade") { screen ->
                when (screen) {
                    Screen.WELCOME -> {
                        WelcomeScreen(
                            onGetStarted = {
                                viewModel.completeOnboarding()
                                onNavigate(Screen.HOME)
                            },
                            onSelectInitialSticker = { sticker ->
                                viewModel.selectActiveSticker(sticker)
                            }
                        )
                    }

                    Screen.HOME -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToStickers = { onNavigate(Screen.STICKERS) },
                            onNavigateToGestures = { onNavigate(Screen.GESTURES) },
                            onNavigateToThemes = { onNavigate(Screen.THEMES) },
                            onNavigateToProfile = { onNavigate(Screen.PROFILE) },
                            onNavigateToSettings = { onNavigate(Screen.PROFILE) },
                            onNavigateToAnimations = { onNavigate(Screen.ANIMATIONS) }
                        )
                    }

                    Screen.ANIMATIONS -> {
                        AnimationsScreen(
                            viewModel = viewModel,
                            onBack = { onNavigate(Screen.HOME) }
                        )
                    }

                    Screen.STICKERS -> {
                        StickersScreen(
                            viewModel = viewModel,
                            onBack = { onNavigate(Screen.HOME) },
                            onCreateNewSticker = { onNavigate(Screen.STICKER_CREATOR) }
                        )
                    }

                    Screen.GESTURES -> {
                        GesturesScreen(
                            viewModel = viewModel,
                            onBack = { onNavigate(Screen.HOME) }
                        )
                    }

                    Screen.PROFILE -> {
                        ProfileScreen(
                            viewModel = viewModel,
                            onBack = { onNavigate(Screen.HOME) }
                        )
                    }

                    Screen.THEMES -> {
                        ThemesScreen(
                            viewModel = viewModel,
                            onBack = { onNavigate(Screen.HOME) }
                        )
                    }

                    Screen.STICKER_CREATOR -> {
                        StickerCreatorScreen(
                            viewModel = viewModel,
                            onBack = { onNavigate(Screen.STICKERS) }
                        )
                    }
                }
            }
        }
    }
}
