package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CutellyTopBar
import com.example.ui.theme.AppThemeSetting
import com.example.ui.theme.CutellyBlush
import com.example.ui.theme.CutellyCardBorder
import com.example.ui.theme.CutellyInk
import com.example.ui.theme.CutellyMint
import com.example.ui.theme.CutellySecondaryText
import com.example.ui.theme.CutellySoftPink
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.UiDensityMode
import com.example.ui.theme.getDarkColorsForTheme
import com.example.ui.theme.getLightColorsForTheme
import com.example.ui.viewmodel.CutellyViewModel
import com.example.util.KawaiiHaptics
import com.example.util.KawaiiSoundManager

data class ThemeCardItem(
    val setting: AppThemeSetting,
    val title: String,
    val description: String,
    val primaryColor: Color,
    val containerColor: Color
)

@Composable
fun ThemesScreen(
    viewModel: CutellyViewModel,
    onBack: () -> Unit
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val uiDensityMode by viewModel.uiDensityMode.collectAsState()
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()
    val isHapticsEnabled by viewModel.isHapticsEnabled.collectAsState()

    val themes = listOf(
        ThemeCardItem(AppThemeSetting.STRAWBERRY_MILK, "Strawberry Milk", "Cute blush pink & soft pastels", Color(0xFFFF78AB), Color(0xFFFFE1ED)),
        ThemeCardItem(AppThemeSetting.LAVENDER_DREAM, "Lavender Dream", "Dreamy purple & lilac skies", Color(0xFF9D7BFF), Color(0xFFEAE1FF)),
        ThemeCardItem(AppThemeSetting.MATCHA_CLOUD, "Matcha Cloud", "Calm sage & fresh herbal mint", Color(0xFF48A873), Color(0xFFDDF8E8)),
        ThemeCardItem(AppThemeSetting.PEACH_BLOSSOM, "Peach Blossom", "Warm coral peach & tender cream", Color(0xFFFF8557), Color(0xFFFFE3D4)),
        ThemeCardItem(AppThemeSetting.BUTTERCREAM, "Buttercream", "Sunlit butter yellow & custard", Color(0xFFE2B024), Color(0xFFFFF0C8)),
        ThemeCardItem(AppThemeSetting.MINTY_BREEZE, "Minty Breeze", "Crisp spearmint & ice pastel", Color(0xFF38B2AC), Color(0xFFD4F7F2)),
        ThemeCardItem(AppThemeSetting.ROSY_DAWN, "Rosy Dawn", "Soft sunrise rose & golden glow", Color(0xFFF06292), Color(0xFFFFDDE6)),
        ThemeCardItem(AppThemeSetting.MIDNIGHT_PLUM, "Midnight Plum", "Cosy deep twilight & neon violet", Color(0xFFB980F0), Color(0xFF3F3057)),
        ThemeCardItem(AppThemeSetting.RETRO_PIXEL, "Retro Pixel", "Vibrant 90s arcade candy pop", Color(0xFFFF5277), Color(0xFFFFE6EB)),
        ThemeCardItem(AppThemeSetting.MINIMAL_CREAM, "Minimal Cream", "Understated warm cream & ink", Color(0xFF30263E), Color(0xFFF4EDE5))
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 76.dp)
        ) {
            CutellyTopBar(
                title = "Themes & Appearance",
                showBack = true,
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Appearance Mode Toggle (Light / Dark / System)
                item {
                    Text(
                        text = "Appearance Mode",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeModeOption(
                                title = "Light",
                                icon = Icons.Default.LightMode,
                                isSelected = themeMode == ThemeMode.LIGHT,
                                modifier = Modifier.weight(1f)
                            ) {
                                viewModel.setThemeMode(ThemeMode.LIGHT)
                            }

                            ThemeModeOption(
                                title = "Dark",
                                icon = Icons.Default.DarkMode,
                                isSelected = themeMode == ThemeMode.DARK,
                                modifier = Modifier.weight(1f)
                            ) {
                                viewModel.setThemeMode(ThemeMode.DARK)
                            }

                            ThemeModeOption(
                                title = "System",
                                icon = Icons.Default.PhoneAndroid,
                                isSelected = themeMode == ThemeMode.SYSTEM,
                                modifier = Modifier.weight(1f)
                            ) {
                                viewModel.setThemeMode(ThemeMode.SYSTEM)
                            }
                        }
                    }
                }

                // Phone Display & Compact Layout Mode Section
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Phone Display & Compact Layout",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Automatically resize padding, cards, and grid density to fit your phone screen perfectly.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            UiDensityMode.values().forEach { mode ->
                                val isSelected = uiDensityMode == mode
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.setUiDensityMode(mode)
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent
                                    ),
                                    border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)) else null
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = when (mode) {
                                                        UiDensityMode.AUTO -> Icons.Default.AspectRatio
                                                        UiDensityMode.COMPACT -> Icons.Default.FitScreen
                                                        UiDensityMode.COMFORTABLE -> Icons.Default.ViewAgenda
                                                    },
                                                    contentDescription = null,
                                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }

                                            Column {
                                                Text(
                                                    text = mode.displayName,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = mode.subtitle,
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Curated Kawaii Palettes
                item {
                    Text(
                        text = "Curated Pastel Themes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(themes) { item ->
                    val isSelected = item.setting == currentTheme
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setTheme(item.setting)
                                KawaiiSoundManager.playSound("chime")
                            },
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
                        border = if (isSelected) {
                            CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
                            )
                        } else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Dual Color Preview Badges
                                Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(item.primaryColor)
                                            .border(2.dp, Color.White, CircleShape)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(item.containerColor)
                                            .border(2.dp, Color.White, CircleShape)
                                    )
                                }

                                Column {
                                    Text(
                                        text = item.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = item.description,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Audio & Haptics Section
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sensory & Feedback",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // Sound Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Sound",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text("Kawaii Sound Effects", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Text("Procedural chimes, bubbles, and pops", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Switch(
                                    checked = isSoundEnabled,
                                    onCheckedChange = { viewModel.toggleSound() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }

                            // Haptics Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Vibration,
                                            contentDescription = "Haptics",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text("Gentle Haptic Feedback", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Text("Subtle clicks and kawaii pulses", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Switch(
                                    checked = isHapticsEnabled,
                                    onCheckedChange = { viewModel.toggleHaptics() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }

                            // Test sounds preview chips
                            if (isSoundEnabled) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Preview Sounds:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        listOf("pop", "bubble", "chime", "sparkle").forEach { sound ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                                                    .clickable {
                                                        KawaiiSoundManager.playSound(sound)
                                                    }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = sound.replaceFirstChar { it.uppercase() },
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeModeOption(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}
