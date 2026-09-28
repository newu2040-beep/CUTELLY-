package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomAnimationItem
import com.example.data.model.StickerAnimationType
import com.example.data.model.StickerItem
import com.example.ui.components.CutellyTopBar
import com.example.ui.stickers.KawaiiStickerView
import com.example.ui.theme.CutellyBackground
import com.example.ui.theme.CutellyBlush
import com.example.ui.theme.CutellyButter
import com.example.ui.theme.CutellyCardBorder
import com.example.ui.theme.CutellyInk
import com.example.ui.theme.CutellyLavender
import com.example.ui.theme.CutellyMint
import com.example.ui.theme.CutellyPeach
import com.example.ui.theme.CutellySecondaryText
import com.example.ui.theme.CutellySoftPink
import com.example.ui.viewmodel.CutellyViewModel
import com.example.util.KawaiiHaptics
import com.example.util.KawaiiSoundManager
import java.io.File
import java.io.FileOutputStream

@Composable
fun StickerCreatorScreen(
    viewModel: CutellyViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val customAnimations by viewModel.customAnimations.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Text/Kaomoji, 1: Photo Import, 2: Shapes

    var stickerName by remember { mutableStateOf("My Sticker") }
    var customText by remember { mutableStateOf("ฅ^•ﻌ•^ฅ") }
    var selectedPhotoPath by remember { mutableStateOf<String?>(null) }
    var selectedShape by remember { mutableStateOf("heart") }
    var selectedAnimation by remember { mutableStateOf("IDLE_BREATHE") }

    val presetColors = listOf(
        Pair("#FFE1ED", "#FF78AB"), // Pink
        Pair("#EAE1FF", "#9D7BFF"), // Lavender
        Pair("#DDF8E8", "#48A873"), // Mint
        Pair("#FFF0C8", "#E2B024"), // Butter
        Pair("#FFE3D4", "#FF8557"), // Peach
        Pair("#FFFFFF", "#30263E")  // Clean
    )
    var selectedColorIndex by remember { mutableIntStateOf(0) }

    val kaomojis = listOf("ฅ^•ﻌ•^ฅ", "(｡♥‿♥｡)", "(◕‿◕✿)", "(づ｡◕‿‿◕｡)づ", "(•̀ᴗ•́)و", "(*^ω^*)", "(´｡• ᵕ •｡`)", "✨ :3 ✨")

    // Zero-permission Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val destFile = File(context.filesDir, "custom_sticker_${System.currentTimeMillis()}.png")
                val outputStream = FileOutputStream(destFile)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                selectedPhotoPath = destFile.absolutePath
                KawaiiSoundManager.playSound("bubble")
            } catch (_: Exception) {}
        }
    }

    // Temporary preview sticker item
    val previewSticker = remember(stickerName, customText, selectedPhotoPath, selectedShape, selectedColorIndex, selectedAnimation, selectedTab) {
        val (bgHex, borderHex) = presetColors[selectedColorIndex]
        StickerItem(
            id = "preview_creator",
            name = stickerName.ifEmpty { "My Sticker" },
            category = "Custom",
            iconType = when (selectedTab) {
                1 -> "custom_image"
                else -> if (selectedTab == 0) "custom_text" else selectedShape
            },
            shapeType = selectedShape,
            customText = customText,
            customImagePath = selectedPhotoPath,
            primaryColorHex = bgHex,
            secondaryColorHex = borderHex,
            defaultAnimation = selectedAnimation
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CutellyBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            CutellyTopBar(
                title = "Sticker Creator",
                showBack = true,
                onBack = onBack
            )

            // Live Preview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.size(160.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val activeCustomAnim = customAnimations.find { it.id == selectedAnimation }
                        KawaiiStickerView(
                            sticker = previewSticker,
                            size = 110.dp,
                            isAnimated = true,
                            forcedAnimation = if (activeCustomAnim == null) selectedAnimation else null,
                            customAnimation = activeCustomAnim
                        )
                    }
                }
            }

            // Tab Switcher
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = CutellyBlush,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CutellyBlush
                    )
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Text & Kaomoji", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Photo Import", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Shapes", fontWeight = FontWeight.Bold) }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sticker Name
                OutlinedTextField(
                    value = stickerName,
                    onValueChange = { stickerName = it },
                    label = { Text("Sticker Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = CutellyBlush,
                        unfocusedIndicatorColor = CutellyCardBorder
                    ),
                    singleLine = true
                )

                when (selectedTab) {
                    0 -> {
                        // Text / Kaomoji editor
                        OutlinedTextField(
                            value = customText,
                            onValueChange = { customText = it },
                            label = { Text("Text or Kaomoji") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = CutellyBlush,
                                unfocusedIndicatorColor = CutellyCardBorder
                            )
                        )

                        Text("Popular Kaomoji", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CutellyInk)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(kaomojis) { k ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.White)
                                        .border(1.dp, CutellyCardBorder, RoundedCornerShape(14.dp))
                                        .clickable {
                                            customText = k
                                            KawaiiSoundManager.playSound("pop")
                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(text = k, fontSize = 13.sp, color = CutellyInk)
                                }
                            }
                        }
                    }

                    1 -> {
                        // Photo Picker
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CutellyLavender)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = CutellyInk)
                                    Text("Pick Photo from Gallery", color = CutellyInk, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = "Zero-permission Android Photo Picker protects your privacy.",
                                fontSize = 12.sp,
                                color = CutellySecondaryText
                            )
                        }
                    }

                    2 -> {
                        // Shape selector
                        Text("Choose Shape", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CutellyInk)
                        val shapes = listOf("heart_pink" to "Heart", "star_yellow" to "Star", "cloud_smiling" to "Cloud", "flower_pink" to "Flower")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            shapes.forEach { (shapeId, label) ->
                                val isSelected = selectedShape == shapeId
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) CutellySoftPink else Color.White)
                                        .border(1.dp, if (isSelected) CutellyBlush else CutellyCardBorder, RoundedCornerShape(16.dp))
                                        .clickable { selectedShape = shapeId }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = CutellyInk
                                    )
                                }
                            }
                        }
                    }
                }

                // Pastel Color Palette Picker
                Text("Color Palette", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CutellyInk)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    presetColors.forEachIndexed { index, (bgHex, borderHex) ->
                        val isSelected = selectedColorIndex == index
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(bgHex)))
                                .border(
                                    width = if (isSelected) 3.dp else 1.5.dp,
                                    color = if (isSelected) CutellyInk else Color(android.graphics.Color.parseColor(borderHex)),
                                    shape = CircleShape
                                )
                                .clickable { selectedColorIndex = index }
                        )
                    }
                }

                // Animation selector
                Text("Idle Animation & Motion Style", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CutellyInk)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Built-in presets
                    items(StickerAnimationType.values().take(6)) { anim ->
                        val isSelected = anim.name == selectedAnimation
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) CutellyBlush else Color.White)
                                .border(1.dp, if (isSelected) CutellyBlush else CutellyCardBorder, RoundedCornerShape(16.dp))
                                .clickable { selectedAnimation = anim.name }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = anim.displayName,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else CutellyInk,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    // Custom animations created by user or presets
                    items(customAnimations) { customAnim ->
                        val isSelected = customAnim.id == selectedAnimation
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) CutellyLavender else Color.White)
                                .border(1.dp, if (isSelected) CutellyLavender else CutellyCardBorder, RoundedCornerShape(16.dp))
                                .clickable { selectedAnimation = customAnim.id }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "✨ ${customAnim.name}",
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else CutellyInk,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Save Button
                Button(
                    onClick = {
                        val (bgHex, borderHex) = presetColors[selectedColorIndex]
                        viewModel.addCustomSticker(
                            name = stickerName,
                            category = "Custom",
                            shapeType = selectedShape,
                            customText = customText,
                            imagePath = selectedPhotoPath,
                            primaryHex = bgHex,
                            secondaryHex = borderHex,
                            animation = selectedAnimation
                        )
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CutellyBlush)
                ) {
                    Text("Save & Add to Screen ✨", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
