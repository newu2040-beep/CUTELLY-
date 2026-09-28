package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.StickerItem
import com.example.data.preseed.PreseededData
import com.example.ui.components.CutellyWordmark
import com.example.ui.stickers.KawaiiStickerView
import com.example.ui.theme.CutellyBackground
import com.example.ui.theme.CutellyBlush
import com.example.ui.theme.CutellyInk
import com.example.ui.theme.CutellyLavender
import com.example.ui.theme.CutellyMint
import com.example.ui.theme.CutellyPeach
import com.example.ui.theme.CutellySecondaryText
import com.example.ui.theme.CutellySoftPink
import com.example.util.KawaiiHaptics
import com.example.util.KawaiiSoundManager
import kotlin.math.roundToInt

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onSelectInitialSticker: (StickerItem) -> Unit = {}
) {
    val context = LocalContext.current
    var showInteractiveOnboarding by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CutellyBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Brand Header
            CutellyWordmark(fontSize = 38)

            // Mascot Illustration
            Box(
                modifier = Modifier
                    .size(290.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                CutellySoftPink.copy(alpha = 0.6f),
                                CutellyLavender.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Procedural Cute Kitten holding pink heart atop phone
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val starterCat = remember {
                        StickerItem(
                            id = "cat_white",
                            name = "Little Cat",
                            category = "Cats",
                            defaultAnimation = "IDLE_BREATHE"
                        )
                    }
                    KawaiiStickerView(
                        sticker = starterCat,
                        size = 150.dp,
                        isAnimated = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    // Soft pastel phone base
                    Box(
                        modifier = Modifier
                            .width(170.dp)
                            .height(55.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(CutellyLavender)
                            .border(2.dp, CutellyInk.copy(alpha = 0.2f), RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CutellyInk.copy(alpha = 0.3f)))
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CutellyBlush))
                            Text(text = "✨ :3", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CutellyInk)
                        }
                    }
                }
            }

            // Headline & Description
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Small stickers. Big possibilities.",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CutellyInk,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Customize your phone, add cute stickers,\nuse smart gestures — make it yours!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = CutellySecondaryText,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            // Primary CTA Button
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        KawaiiHaptics.performSuccessBurst(context)
                        KawaiiSoundManager.playSound("sparkle")
                        showInteractiveOnboarding = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(6.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CutellyInk,
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Get Started",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Footer Credit
                Text(
                    text = "Made with love by Rahul Shah ❤️",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = CutellySecondaryText
                )
            }
        }
    }

    if (showInteractiveOnboarding) {
        InteractiveOnboardingDialog(
            onDismiss = {
                showInteractiveOnboarding = false
                onGetStarted()
            },
            onComplete = { selectedSticker ->
                onSelectInitialSticker(selectedSticker)
                showInteractiveOnboarding = false
                onGetStarted()
            }
        )
    }
}

@Composable
fun InteractiveOnboardingDialog(
    onDismiss: () -> Unit,
    onComplete: (StickerItem) -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(1) }
    val starterStickers = remember {
        PreseededData.preseededStickers.take(6)
    }
    var selectedSticker by remember { mutableStateOf(starterStickers[0]) }

    // Mock phone position coordinates
    var stickerOffsetX by remember { mutableFloatStateOf(60f) }
    var stickerOffsetY by remember { mutableFloatStateOf(80f) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Step Indicator Pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..3) {
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (i == currentStep) 24.dp else 10.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (i == currentStep) CutellyBlush else CutellySoftPink)
                        )
                    }
                }

                when (currentStep) {
                    1 -> {
                        // Step 1: Choose Sticker
                        Text(
                            text = "Choose your sticker ✨",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CutellyInk
                        )
                        Text(
                            text = "Pick your favorite companion to float on your screen.",
                            fontSize = 13.sp,
                            color = CutellySecondaryText,
                            textAlign = TextAlign.Center
                        )

                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(CutellySoftPink.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            KawaiiStickerView(
                                sticker = selectedSticker,
                                size = 80.dp,
                                isAnimated = true
                            )
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            items(starterStickers) { sticker ->
                                val isSelected = sticker.id == selectedSticker.id
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) CutellySoftPink else CutellyBackground)
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) CutellyBlush else Color.Transparent,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            selectedSticker = sticker
                                            KawaiiSoundManager.playSound("pop")
                                            KawaiiHaptics.performClick(context)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    KawaiiStickerView(
                                        sticker = sticker,
                                        size = 40.dp,
                                        isAnimated = false
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                KawaiiSoundManager.playSound("bubble")
                                currentStep = 2
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CutellyInk)
                        ) {
                            Text("Next: Position Sticker", fontWeight = FontWeight.Bold)
                        }
                    }

                    2 -> {
                        // Step 2: Interactive positioning preview
                        Text(
                            text = "Position your sticker 📱",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CutellyInk
                        )
                        Text(
                            text = "Drag the sticker anywhere on the phone preview!",
                            fontSize = 13.sp,
                            color = CutellySecondaryText,
                            textAlign = TextAlign.Center
                        )

                        // Mini Interactive Phone Mockup
                        Box(
                            modifier = Modifier
                                .width(200.dp)
                                .height(220.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(CutellyLavender.copy(alpha = 0.35f))
                                .border(2.dp, CutellyInk.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                        ) {
                            // Camera notch
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(CutellyInk.copy(alpha = 0.3f))
                                    .align(Alignment.TopCenter)
                                    .offset(y = 8.dp)
                            )

                            // Draggable sticker
                            Box(
                                modifier = Modifier
                                    .offset { IntOffset(stickerOffsetX.roundToInt(), stickerOffsetY.roundToInt()) }
                                    .pointerInput(Unit) {
                                        detectDragGestures { change, dragAmount ->
                                            change.consume()
                                            stickerOffsetX = (stickerOffsetX + dragAmount.x).coerceIn(0f, 130f)
                                            stickerOffsetY = (stickerOffsetY + dragAmount.y).coerceIn(20f, 150f)
                                        }
                                    }
                            ) {
                                KawaiiStickerView(
                                    sticker = selectedSticker,
                                    size = 54.dp,
                                    isAnimated = true
                                )
                            }
                        }

                        Button(
                            onClick = {
                                KawaiiSoundManager.playSound("chime")
                                currentStep = 3
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CutellyInk)
                        ) {
                            Text("Next: Enable & Start", fontWeight = FontWeight.Bold)
                        }
                    }

                    3 -> {
                        // Step 3: Permissions & Finish
                        Text(
                            text = "Allow Floating Overlay ✨",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CutellyInk
                        )
                        Text(
                            text = "To appear above other apps, CUTELLY requires the Android 'Display over other apps' permission.",
                            fontSize = 13.sp,
                            color = CutellySecondaryText,
                            textAlign = TextAlign.Center
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CutellySoftPink.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "🔒 100% Offline & Private.\nNo ads, no accounts, and no data leaves your phone.",
                                fontSize = 12.sp,
                                color = CutellyInk,
                                lineHeight = 18.sp
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (!Settings.canDrawOverlays(context)) {
                                        val intent = Intent(
                                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                            Uri.parse("package:${context.packageName}")
                                        ).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(intent)
                                    }
                                    onComplete(selectedSticker)
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CutellyBlush)
                            ) {
                                Text("Grant Permission & Start", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    onComplete(selectedSticker)
                                },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text("Skip for now", color = CutellyInk, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}
