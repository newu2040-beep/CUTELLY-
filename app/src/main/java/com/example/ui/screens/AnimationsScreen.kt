package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomAnimationItem
import com.example.data.model.StickerItem
import com.example.ui.components.CutellyTopBar
import com.example.ui.stickers.KawaiiStickerView
import com.example.ui.theme.CutellyBackground
import com.example.ui.theme.CutellyBlush
import com.example.ui.theme.CutellyCardBorder
import com.example.ui.theme.CutellyInk
import com.example.ui.theme.CutellyLavender
import com.example.ui.theme.CutellyMint
import com.example.ui.theme.CutellyPeach
import com.example.ui.theme.CutellySecondaryText
import com.example.ui.theme.CutellySoftPink
import com.example.ui.theme.LocalCutellyLayout
import com.example.ui.viewmodel.CutellyViewModel
import com.example.util.KawaiiHaptics
import com.example.util.KawaiiSoundManager
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimationsScreen(
    viewModel: CutellyViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val customAnimations by viewModel.customAnimations.collectAsState()
    val activeConfig by viewModel.activeFloatingConfig.collectAsState()
    val allStickers by viewModel.allStickers.collectAsState()

    var selectedPreviewSticker by remember(allStickers) {
        mutableStateOf(allStickers.firstOrNull() ?: StickerItem("cat_white", "Little Cat", "Cats"))
    }

    var selectedAnimation by remember(customAnimations, activeConfig) {
        val activeAnimId = activeConfig?.customAnimationId
        val initial = customAnimations.find { it.id == activeAnimId }
            ?: customAnimations.firstOrNull()
            ?: CustomAnimationItem("preset_jelly", "Hyper Jelly Bounce", "BOUNCE", 700, 0.88f, 1.14f, 14f, 0f, 6f, "sparkles", "BOUNCY", true)
        mutableStateOf(initial)
    }

    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var showCreatorSheet by remember { mutableStateOf(false) }
    var animationToEdit by remember { mutableStateOf<CustomAnimationItem?>(null) }

    val filterCategories = listOf("All", "Custom", "Bouncy", "Dreamy", "Sparks")

    val filteredAnimations = remember(customAnimations, selectedCategoryFilter) {
        when (selectedCategoryFilter) {
            "All" -> customAnimations
            "Custom" -> customAnimations.filter { !it.isPreset }
            "Bouncy" -> customAnimations.filter { it.baseType in listOf("BOUNCE", "SQUISH", "JITTER") }
            "Dreamy" -> customAnimations.filter { it.baseType in listOf("FLOAT", "SWAY", "HEARTBEAT") }
            "Sparks" -> customAnimations.filter { it.particleType in listOf("sparkles", "stars", "hearts") }
            else -> customAnimations
        }
    }

    val isActiveOnFloating = activeConfig?.customAnimationId == selectedAnimation.id

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val layout = LocalCutellyLayout.current

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = if (layout.isCompact) 75.dp else 90.dp)
            ) {
                // Top Bar
                CutellyTopBar(
                    title = "Animation Studio",
                    showBack = true,
                    onBack = onBack
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = layout.contentPadding)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(layout.itemSpacing)
                ) {
                    // Hero Interactive Live Preview Stage
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(layout.cornerRadius),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(if (layout.isCompact) 14.dp else 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(if (layout.isCompact) 10.dp else 14.dp)
                        ) {
                            // Badge indicating active state
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "LIVE MOTION PREVIEW",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        letterSpacing = 1.sp
                                    )
                                }

                                if (isActiveOnFloating) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "On Your Screen ✨",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }

                            // Glowing Preview Stage
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                            )
                                        )
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                KawaiiStickerView(
                                    sticker = selectedPreviewSticker,
                                    size = 96.dp,
                                    isAnimated = true,
                                    customAnimation = selectedAnimation
                                )
                            }

                            // Animation Title & Archetype Info
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = selectedAnimation.name,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${selectedAnimation.baseType} Motion • ${selectedAnimation.durationMs}ms • ${selectedAnimation.particleType.replaceFirstChar { it.uppercase() }} Particles",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Switch Preview Character chips
                            Text(
                                text = "Preview Character:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(allStickers.take(8)) { sticker ->
                                    val isSelected = sticker.id == selectedPreviewSticker.id
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                            .border(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .clickable {
                                                selectedPreviewSticker = sticker
                                                KawaiiSoundManager.playSound("pop")
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = sticker.name,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Main Action: Apply to Floating Screen Sticker
                            Button(
                                onClick = {
                                    viewModel.applyAnimationToActiveOverlay(selectedAnimation.id)
                                    KawaiiSoundManager.playSound("sparkle")
                                    KawaiiHaptics.performSuccessBurst(context)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isActiveOnFloating) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isActiveOnFloating) Icons.Default.Check else Icons.Default.PlayArrow,
                                        contentDescription = null
                                    )
                                    Text(
                                        text = if (isActiveOnFloating) "Active on Screen ✨" else "Apply to Screen Sticker ✨",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    // Category Filter Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Animation Library",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "+ New Custom",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                animationToEdit = null
                                showCreatorSheet = true
                                KawaiiSoundManager.playSound("bubble")
                            }
                        )
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filterCategories) { category ->
                            val isSelected = category == selectedCategoryFilter
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedCategoryFilter = category
                                    KawaiiSoundManager.playSound("pop")
                                },
                                label = {
                                    Text(
                                        text = category,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }

                    // Animations Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(((filteredAnimations.size + 1) / 2 * 190).coerceAtLeast(200).dp),
                        userScrollEnabled = false
                    ) {
                        items(filteredAnimations, key = { it.id }) { anim ->
                            val isSelected = anim.id == selectedAnimation.id
                            val isAppliedToScreen = activeConfig?.customAnimationId == anim.id

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedAnimation = anim
                                        KawaiiSoundManager.playSound("bubble")
                                        KawaiiHaptics.performClick(context)
                                    },
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))) else null,
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.5.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Header: Preset vs Custom & Edit/Delete
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (anim.isPreset) "PRESET" else "CUSTOM",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (anim.isPreset) MaterialTheme.colorScheme.primary else CutellyPeach
                                        )

                                        if (!anim.isPreset) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                IconButton(
                                                    onClick = {
                                                        animationToEdit = anim
                                                        showCreatorSheet = true
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Edit,
                                                        contentDescription = "Edit",
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                                IconButton(
                                                    onClick = {
                                                        viewModel.deleteCustomAnimation(anim.id)
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Delete",
                                                        tint = MaterialTheme.colorScheme.error,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        } else if (isAppliedToScreen) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Applied",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    // Mini Live Preview
                                    Box(
                                        modifier = Modifier
                                            .size(68.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        KawaiiStickerView(
                                            sticker = selectedPreviewSticker,
                                            size = 46.dp,
                                            isAnimated = true,
                                            customAnimation = anim
                                        )
                                    }

                                    // Name & Archetype
                                    Text(
                                        text = anim.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        text = "${anim.baseType} • ${anim.durationMs}ms",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Floating Action Button to launch Creator Studio
            FloatingActionButton(
                onClick = {
                    animationToEdit = null
                    showCreatorSheet = true
                    KawaiiSoundManager.playSound("bubble")
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Animation")
                    Text("New Animation ✨", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }

    // Modal Bottom Sheet: Custom Animation Studio & Creator
    if (showCreatorSheet) {
        CustomAnimationCreatorSheet(
            animationToEdit = animationToEdit,
            sampleSticker = selectedPreviewSticker,
            onDismiss = { showCreatorSheet = false },
            onSave = { name, baseType, durationMs, minScale, maxScale, translateY, translateX, rotation, particle, easing, applyNow ->
                viewModel.createOrUpdateCustomAnimation(
                    id = animationToEdit?.id,
                    name = name,
                    baseType = baseType,
                    durationMs = durationMs,
                    minScale = minScale,
                    maxScale = maxScale,
                    translateYDp = translateY,
                    translateXDp = translateX,
                    rotationAngleDeg = rotation,
                    particleType = particle,
                    easingType = easing
                )
                if (applyNow) {
                    // Will take effect once inserted
                    val targetId = animationToEdit?.id ?: "custom_anim_${System.currentTimeMillis()}"
                    viewModel.applyAnimationToActiveOverlay(targetId)
                }
                showCreatorSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAnimationCreatorSheet(
    animationToEdit: CustomAnimationItem?,
    sampleSticker: StickerItem,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        baseType: String,
        durationMs: Int,
        minScale: Float,
        maxScale: Float,
        translateYDp: Float,
        translateXDp: Float,
        rotationAngleDeg: Float,
        particleType: String,
        easingType: String,
        applyNow: Boolean
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Form state
    var name by remember {
        mutableStateOf(animationToEdit?.name ?: getRandomCuteAnimationName())
    }
    var baseType by remember {
        mutableStateOf(animationToEdit?.baseType ?: "BOUNCE")
    }
    var durationMs by remember {
        mutableIntStateOf(animationToEdit?.durationMs ?: 800)
    }
    var minScale by remember {
        mutableFloatStateOf(animationToEdit?.minScale ?: 0.90f)
    }
    var maxScale by remember {
        mutableFloatStateOf(animationToEdit?.maxScale ?: 1.12f)
    }
    var translateYDp by remember {
        mutableFloatStateOf(animationToEdit?.translateYDp ?: 10f)
    }
    var translateXDp by remember {
        mutableFloatStateOf(animationToEdit?.translateXDp ?: 0f)
    }
    var rotationAngleDeg by remember {
        mutableFloatStateOf(animationToEdit?.rotationAngleDeg ?: 8f)
    }
    var particleType by remember {
        mutableStateOf(animationToEdit?.particleType ?: "sparkles")
    }
    var easingType by remember {
        mutableStateOf(animationToEdit?.easingType ?: "BOUNCY")
    }

    // Ephemeral dynamic preview animation object
    val livePreviewAnimation = remember(name, baseType, durationMs, minScale, maxScale, translateYDp, translateXDp, rotationAngleDeg, particleType, easingType) {
        CustomAnimationItem(
            id = "preview_temp",
            name = name,
            baseType = baseType,
            durationMs = durationMs,
            minScale = minScale,
            maxScale = maxScale,
            translateYDp = translateYDp,
            translateXDp = translateXDp,
            rotationAngleDeg = rotationAngleDeg,
            particleType = particleType,
            easingType = easingType
        )
    }

    val archetypes = listOf(
        "BOUNCE" to "Spring Hop",
        "FLOAT" to "Levitation",
        "PULSE" to "Heartbeat",
        "WIGGLE" to "Excited Tilt",
        "SPIN" to "Pirouette",
        "SWAY" to "Pendulum",
        "SQUISH" to "Squishy Jelly",
        "JITTER" to "Micro Shake"
    )

    val particles = listOf(
        "none" to "None",
        "sparkles" to "Sparkles ✨",
        "hearts" to "Hearts 💖",
        "stars" to "Stars ⭐",
        "bubbles" to "Bubbles 🫧",
        "notes" to "Music 🎵",
        "zzz" to "Sleepy 💤"
    )

    val easings = listOf("BOUNCY" to "Bouncy", "SMOOTH" to "Smooth", "LINEAR" to "Linear")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (animationToEdit != null) "Edit Custom Animation ✨" else "Animation Studio Studio ✨",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Real-Time Dynamic Live Preview Disc
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                    .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                KawaiiStickerView(
                    sticker = sampleSticker,
                    size = 80.dp,
                    isAnimated = true,
                    customAnimation = livePreviewAnimation
                )
            }

            Text(
                text = "Live preview updates dynamically with every slider tweak!",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Name Input with Dice Randomizer
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Animation Name") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    singleLine = true
                )

                IconButton(
                    onClick = {
                        name = getRandomCuteAnimationName()
                        KawaiiSoundManager.playSound("bubble")
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Randomize Name",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Archetype Selector
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Motion Archetype",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(archetypes) { (type, label) ->
                        val isSelected = baseType == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable {
                                    baseType = type
                                    // Apply archetypal defaults
                                    when (type) {
                                        "BOUNCE" -> { translateYDp = 12f; minScale = 0.90f; maxScale = 1.10f; durationMs = 700 }
                                        "FLOAT" -> { translateYDp = 8f; minScale = 0.98f; maxScale = 1.02f; durationMs = 1500; easingType = "SMOOTH" }
                                        "PULSE" -> { translateYDp = 0f; minScale = 0.85f; maxScale = 1.20f; durationMs = 600 }
                                        "WIGGLE" -> { rotationAngleDeg = 14f; durationMs = 400; translateYDp = 0f }
                                        "SPIN" -> { rotationAngleDeg = 360f; durationMs = 2500; easingType = "LINEAR" }
                                        "SWAY" -> { rotationAngleDeg = 10f; translateXDp = 10f; durationMs = 1200 }
                                        "SQUISH" -> { minScale = 0.80f; maxScale = 1.25f; durationMs = 650; translateYDp = 4f }
                                        "JITTER" -> { translateXDp = 5f; translateYDp = 4f; rotationAngleDeg = 6f; durationMs = 250 }
                                    }
                                    KawaiiSoundManager.playSound("pop")
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Speed / Duration Slider
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Duration / Speed", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("${durationMs}ms", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Slider(
                    value = durationMs.toFloat(),
                    onValueChange = { durationMs = it.toInt() },
                    valueRange = 200f..3000f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            // Min & Max Scale Sliders
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Min Scale (${String.format("%.2f", minScale)}x)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Slider(
                        value = minScale,
                        onValueChange = { minScale = it },
                        valueRange = 0.70f..1.00f,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Max Scale (${String.format("%.2f", maxScale)}x)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Slider(
                        value = maxScale,
                        onValueChange = { maxScale = it },
                        valueRange = 1.00f..1.45f,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                    )
                }
            }

            // Vertical & Horizontal Movement Sliders
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Vertical Hop (${translateYDp.toInt()}dp)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Slider(
                        value = translateYDp,
                        onValueChange = { translateYDp = it },
                        valueRange = -25f..25f,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Horizontal Drift (${translateXDp.toInt()}dp)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Slider(
                        value = translateXDp,
                        onValueChange = { translateXDp = it },
                        valueRange = -25f..25f,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                    )
                }
            }

            // Rotation Tilt Slider
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Rotation / Tilt Angle", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("${rotationAngleDeg.toInt()}°", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Slider(
                    value = rotationAngleDeg,
                    onValueChange = { rotationAngleDeg = it },
                    valueRange = 0f..360f,
                    colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                )
            }

            // Particle Effects Selection
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Aesthetic Particle Atmosphere",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(particles) { (type, label) ->
                        val isSelected = particleType == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    particleType = type
                                    KawaiiSoundManager.playSound("sparkle")
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Easing Curve
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Motion Easing Curve",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    easings.forEach { (type, label) ->
                        val isSelected = easingType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable {
                                    easingType = type
                                    KawaiiSoundManager.playSound("pop")
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Save & Save + Apply
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        onSave(name, baseType, durationMs, minScale, maxScale, translateYDp, translateXDp, rotationAngleDeg, particleType, easingType, true)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save & Apply to Screen ✨", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                OutlinedButton(
                    onClick = {
                        onSave(name, baseType, durationMs, minScale, maxScale, translateYDp, translateXDp, rotationAngleDeg, particleType, easingType, false)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Save to Library Only", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun getRandomCuteAnimationName(): String {
    val prefixes = listOf("Jelly", "Cosmic", "Dreamy", "Cotton Candy", "Sugar", "Starry", "Moonlight", "Pop", "Bubbly", "Honey", "Sakura", "Glitter")
    val suffixes = listOf("Bounce", "Flutter", "Twirl", "Hop", "Pulse", "Wiggle", "Sway", "Jiggly", "Float", "Sparkle", "Breeze", "Chime")
    val prefix = prefixes[Random.nextInt(prefixes.size)]
    val suffix = suffixes[Random.nextInt(suffixes.size)]
    return "$prefix $suffix"
}
