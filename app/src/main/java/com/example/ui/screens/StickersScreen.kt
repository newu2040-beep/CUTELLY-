package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomAnimationItem
import com.example.data.model.StickerAnimationType
import com.example.data.model.StickerItem
import com.example.ui.components.CutellyTopBar
import com.example.ui.stickers.KawaiiStickerView
import com.example.ui.theme.CutellyBackground
import com.example.ui.theme.CutellyBlush
import com.example.ui.theme.CutellyCardBorder
import com.example.ui.theme.CutellyInk
import com.example.ui.theme.CutellyLavender
import com.example.ui.theme.CutellySecondaryText
import com.example.ui.theme.CutellySoftPink
import com.example.ui.viewmodel.CutellyViewModel
import com.example.util.KawaiiHaptics
import com.example.util.KawaiiSoundManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickersScreen(
    viewModel: CutellyViewModel,
    onBack: () -> Unit,
    onCreateNewSticker: () -> Unit
) {
    val context = LocalContext.current
    val allStickers by viewModel.allStickers.collectAsState()
    val activeConfig by viewModel.activeFloatingConfig.collectAsState()
    val isOverlayActive by viewModel.isOverlayActive.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val customAnimations by viewModel.customAnimations.collectAsState()

    var isSearchExpanded by remember { mutableStateOf(false) }
    var detailSticker by remember { mutableStateOf<StickerItem?>(null) }

    val categories = listOf("All", "Custom", "Cute", "Animals", "Food", "More")

    val filteredStickers = remember(allStickers, selectedCategory, searchQuery) {
        allStickers.filter { sticker ->
            val matchesCategory = when (selectedCategory) {
                "All" -> true
                "Custom" -> sticker.isUserCreated || sticker.category.equals("Custom", ignoreCase = true)
                "Cute" -> sticker.category in listOf("Cats", "Bunnies", "Hearts", "Flowers")
                "Animals" -> sticker.category in listOf("Cats", "Bunnies", "Bears", "Pandas", "Frogs", "Animals")
                "Food" -> sticker.category == "Food"
                "More" -> sticker.category in listOf("Ghosts", "Stars", "Clouds", "Retro Pixel", "Doodles")
                else -> true
            }
            val matchesSearch = searchQuery.isEmpty() ||
                    sticker.name.contains(searchQuery, ignoreCase = true) ||
                    sticker.category.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 76.dp)
            ) {
                // Top Bar
                CutellyTopBar(
                    title = "Stickers",
                    showBack = true,
                    onBack = onBack,
                    showSearch = true,
                    onSearch = { isSearchExpanded = !isSearchExpanded }
                )

                // Search field if expanded
                AnimatedVisibility(visible = isSearchExpanded) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search stickers...", color = CutellySecondaryText) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = CutellyBlush,
                            unfocusedIndicatorColor = CutellyCardBorder
                        ),
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true
                    )
                }

                // Category Chips (Row of Pills)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) CutellyBlush else Color.White)
                                .border(
                                    1.dp,
                                    if (isSelected) CutellyBlush else CutellyCardBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    viewModel.setCategory(category)
                                    KawaiiSoundManager.playSound("pop")
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = category,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else CutellyInk
                            )
                        }
                    }
                }

                // 4-Column Responsive Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredStickers, key = { it.id }) { sticker ->
                        val isCurrentActive = activeConfig?.stickerId == sticker.id
                        Card(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable {
                                    viewModel.selectActiveSticker(sticker)
                                    if (!isOverlayActive && viewModel.canDrawOverlays()) {
                                        viewModel.toggleOverlayService {}
                                    }
                                    Toast.makeText(context, "✨ ${sticker.name} is now floating on screen!", Toast.LENGTH_SHORT).show()
                                    KawaiiHaptics.performClick(context)
                                    KawaiiSoundManager.playSound("pop")
                                },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrentActive) CutellySoftPink else Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentActive) 3.dp else 1.dp),
                            border = if (isCurrentActive) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CutellyBlush)) else null
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                KawaiiStickerView(
                                    sticker = sticker,
                                    size = 48.dp,
                                    isAnimated = isCurrentActive
                                )

                                if (isCurrentActive) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(4.dp)
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(CutellyBlush),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Active",
                                            tint = Color.White,
                                            modifier = Modifier.size(11.dp)
                                        )
                                    }
                                }

                                if (sticker.isFavorite) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Favorite",
                                        tint = CutellyBlush,
                                        modifier = Modifier
                                            .size(12.dp)
                                            .align(Alignment.TopEnd)
                                            .padding(top = 4.dp, end = 4.dp)
                                    )
                                }

                                // Tune / Motion Customizer Button
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(4.dp)
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                                        .clickable {
                                            detailSticker = sticker
                                            KawaiiSoundManager.playSound("bubble")
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = "Customize Motion",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Sticky Button: "✨ Create Your Own Sticker"
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = {
                        KawaiiSoundManager.playSound("sparkle")
                        KawaiiHaptics.performSuccessBurst(context)
                        onCreateNewSticker()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(8.dp, RoundedCornerShape(26.dp)),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CutellyBlush,
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "✨  Create Your Own Sticker",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Sticker Details Bottom Sheet
    detailSticker?.let { sticker ->
        StickerDetailBottomSheet(
            sticker = sticker,
            customAnimations = customAnimations,
            isActive = activeConfig?.stickerId == sticker.id,
            onDismiss = { detailSticker = null },
            onSelectActive = { selectedAnimId ->
                viewModel.selectActiveSticker(sticker)
                val isCustom = customAnimations.any { it.id == selectedAnimId }
                if (isCustom) {
                    viewModel.applyAnimationToActiveOverlay(selectedAnimId)
                } else {
                    viewModel.applyAnimationToActiveOverlay(null)
                }
                detailSticker = null
            },
            onToggleFavorite = {
                viewModel.toggleFavorite(sticker)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickerDetailBottomSheet(
    sticker: StickerItem,
    customAnimations: List<CustomAnimationItem> = emptyList(),
    isActive: Boolean,
    onDismiss: () -> Unit,
    onSelectActive: (String) -> Unit,
    onToggleFavorite: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var selectedAnim by remember { mutableStateOf(sticker.defaultAnimation) }
    val activeCustomAnim = remember(selectedAnim, customAnimations) {
        customAnimations.find { it.id == selectedAnim }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Preview Circle
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                KawaiiStickerView(
                    sticker = sticker,
                    size = 90.dp,
                    isAnimated = true,
                    forcedAnimation = if (activeCustomAnim == null) selectedAnim else null,
                    customAnimation = activeCustomAnim
                )
            }

            // Title & Category
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = sticker.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Category: ${sticker.category}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Animation Selector Chips (Built-in + Custom animations)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Motion & Animation Style",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Built-ins
                    items(StickerAnimationType.values()) { anim ->
                        val isSelected = anim.name == selectedAnim
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(16.dp))
                                .clickable { selectedAnim = anim.name }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = anim.displayName,
                                fontSize = 12.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    // Custom motions
                    items(customAnimations) { customAnim ->
                        val isSelected = customAnim.id == selectedAnim
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .border(1.dp, if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent, RoundedCornerShape(16.dp))
                                .clickable { selectedAnim = customAnim.id }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "✨ ${customAnim.name}",
                                fontSize = 12.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = if (sticker.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = { onSelectActive(selectedAnim) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = if (isActive) "Active On Screen ✨" else "Add to Screen",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
