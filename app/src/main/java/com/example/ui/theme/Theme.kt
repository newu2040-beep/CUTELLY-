package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class UiDensityMode(val displayName: String, val subtitle: String) {
    AUTO("Auto Adaptive", "Dynamically fits your phone screen dimensions"),
    COMPACT("Compact Mode", "Slim padding, smaller cards, fits more on screen"),
    COMFORTABLE("Comfortable", "Spacious airy layouts with plush padding")
}

data class CutellyLayoutConfig(
    val isCompact: Boolean,
    val contentPadding: Dp,
    val cardPadding: Dp,
    val itemSpacing: Dp,
    val sectionSpacing: Dp,
    val stickerGridCols: Int,
    val heroScale: Float,
    val iconSize: Dp,
    val cornerRadius: Dp
)

val LocalCutellyLayout = compositionLocalOf {
    CutellyLayoutConfig(
        isCompact = false,
        contentPadding = 16.dp,
        cardPadding = 16.dp,
        itemSpacing = 12.dp,
        sectionSpacing = 16.dp,
        stickerGridCols = 2,
        heroScale = 1.0f,
        iconSize = 24.dp,
        cornerRadius = 24.dp
    )
}

enum class AppThemeSetting(val title: String, val subtitle: String) {
    STRAWBERRY_MILK("Strawberry Milk", "Cute blush pink & soft pastels"),
    LAVENDER_DREAM("Lavender Dream", "Dreamy purple & lilac skies"),
    MATCHA_CLOUD("Matcha Cloud", "Calm sage & fresh herbal mint"),
    PEACH_BLOSSOM("Peach Blossom", "Warm coral peach & tender cream"),
    BUTTERCREAM("Buttercream", "Sunlit butter yellow & custard"),
    MINTY_BREEZE("Minty Breeze", "Crisp spearmint & ice pastel"),
    ROSY_DAWN("Rosy Dawn", "Soft sunrise rose & golden glow"),
    MIDNIGHT_PLUM("Midnight Plum", "Cosy deep twilight & neon violet"),
    RETRO_PIXEL("Retro Pixel", "Vibrant 90s arcade candy pop"),
    MINIMAL_CREAM("Minimal Cream", "Understated warm cream & ink")
}

fun getLightColorsForTheme(theme: AppThemeSetting): androidx.compose.material3.ColorScheme {
    val primary = when (theme) {
        AppThemeSetting.STRAWBERRY_MILK -> CutellyBlush
        AppThemeSetting.LAVENDER_DREAM -> ThemeLavenderPrimary
        AppThemeSetting.MATCHA_CLOUD -> ThemeMatchaPrimary
        AppThemeSetting.PEACH_BLOSSOM -> ThemePeachPrimary
        AppThemeSetting.BUTTERCREAM -> ThemeButterPrimary
        AppThemeSetting.MINTY_BREEZE -> ThemeMintyPrimary
        AppThemeSetting.ROSY_DAWN -> ThemeRosyPrimary
        AppThemeSetting.MIDNIGHT_PLUM -> ThemePlumPrimary
        AppThemeSetting.RETRO_PIXEL -> Color(0xFFFF5277)
        AppThemeSetting.MINIMAL_CREAM -> CutellyInk
    }
    val container = when (theme) {
        AppThemeSetting.STRAWBERRY_MILK -> CutellySoftPink
        AppThemeSetting.LAVENDER_DREAM -> CutellyLavender
        AppThemeSetting.MATCHA_CLOUD -> CutellyMint
        AppThemeSetting.PEACH_BLOSSOM -> CutellyPeach
        AppThemeSetting.BUTTERCREAM -> CutellyButter
        AppThemeSetting.MINTY_BREEZE -> ThemeMintyContainer
        AppThemeSetting.ROSY_DAWN -> ThemeRosyContainer
        AppThemeSetting.MIDNIGHT_PLUM -> ThemePlumContainer
        AppThemeSetting.RETRO_PIXEL -> Color(0xFFFFE6EB)
        AppThemeSetting.MINIMAL_CREAM -> Color(0xFFF4EDE5)
    }

    return lightColorScheme(
        primary = primary,
        onPrimary = Color.White,
        primaryContainer = container,
        onPrimaryContainer = CutellyInk,
        secondary = CutellyLavender,
        onSecondary = CutellyInk,
        secondaryContainer = CutellyMint,
        onSecondaryContainer = CutellyInk,
        tertiary = CutellyPeach,
        onTertiary = CutellyInk,
        background = CutellyBackground,
        onBackground = CutellyInk,
        surface = CutellySurface,
        onSurface = CutellyInk,
        surfaceVariant = container,
        onSurfaceVariant = CutellySecondaryText,
        outline = CutellyCardBorder,
        outlineVariant = CutellySecondaryText.copy(alpha = 0.2f)
    )
}

fun getDarkColorsForTheme(theme: AppThemeSetting): androidx.compose.material3.ColorScheme {
    val primary = when (theme) {
        AppThemeSetting.STRAWBERRY_MILK -> Color(0xFFFF8AB5)
        AppThemeSetting.LAVENDER_DREAM -> Color(0xFFBCA6FF)
        AppThemeSetting.MATCHA_CLOUD -> Color(0xFF6EDFA3)
        AppThemeSetting.PEACH_BLOSSOM -> Color(0xFFFF9E7A)
        AppThemeSetting.BUTTERCREAM -> Color(0xFFFFD464)
        AppThemeSetting.MINTY_BREEZE -> Color(0xFF4FD1C5)
        AppThemeSetting.ROSY_DAWN -> Color(0xFFFF80AB)
        AppThemeSetting.MIDNIGHT_PLUM -> Color(0xFFC792EA)
        AppThemeSetting.RETRO_PIXEL -> Color(0xFFFF7597)
        AppThemeSetting.MINIMAL_CREAM -> Color(0xFFE5DCD3)
    }

    return darkColorScheme(
        primary = primary,
        onPrimary = CutellyDarkBackground,
        primaryContainer = CutellyDarkSurfaceVariant,
        onPrimaryContainer = CutellyDarkInk,
        secondary = CutellyLavender.copy(alpha = 0.6f),
        onSecondary = CutellyDarkInk,
        secondaryContainer = CutellyDarkSurfaceVariant,
        onSecondaryContainer = CutellyDarkInk,
        tertiary = CutellyPeach.copy(alpha = 0.6f),
        onTertiary = CutellyDarkInk,
        background = CutellyDarkBackground,
        onBackground = CutellyDarkInk,
        surface = CutellyDarkSurface,
        onSurface = CutellyDarkInk,
        surfaceVariant = CutellyDarkSurfaceVariant,
        onSurfaceVariant = CutellyDarkSecondaryText,
        outline = CutellyDarkBorder,
        outlineVariant = CutellyDarkBorder.copy(alpha = 0.5f)
    )
}

val CutellyShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    activeTheme: AppThemeSetting = AppThemeSetting.STRAWBERRY_MILK,
    uiDensityMode: UiDensityMode = UiDensityMode.AUTO,
    content: @Composable () -> Unit,
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp

    val isCompactScreen = screenWidthDp < 380 || screenHeightDp < 720
    val isCompact = when (uiDensityMode) {
        UiDensityMode.COMPACT -> true
        UiDensityMode.COMFORTABLE -> false
        UiDensityMode.AUTO -> isCompactScreen
    }

    val layoutConfig = CutellyLayoutConfig(
        isCompact = isCompact,
        contentPadding = if (isCompact) 10.dp else if (screenWidthDp > 600) 24.dp else 16.dp,
        cardPadding = if (isCompact) 12.dp else if (screenWidthDp > 600) 20.dp else 16.dp,
        itemSpacing = if (isCompact) 8.dp else 12.dp,
        sectionSpacing = if (isCompact) 10.dp else 16.dp,
        stickerGridCols = if (screenWidthDp > 600) 4 else if (screenWidthDp > 420) 3 else 2,
        heroScale = if (isCompact) 0.82f else 1.0f,
        iconSize = if (isCompact) 20.dp else 24.dp,
        cornerRadius = if (isCompact) 18.dp else 24.dp
    )

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> getDarkColorsForTheme(activeTheme)
        else -> getLightColorsForTheme(activeTheme)
    }

    CompositionLocalProvider(LocalCutellyLayout provides layoutConfig) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = CutellyShapes,
            content = content
        )
    }
}
