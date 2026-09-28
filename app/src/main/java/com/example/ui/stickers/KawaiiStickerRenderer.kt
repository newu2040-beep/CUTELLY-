package com.example.ui.stickers

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.CustomAnimationItem
import com.example.data.model.StickerAnimationType
import com.example.data.model.StickerItem
import com.example.ui.theme.CutellyBlush
import com.example.ui.theme.CutellyInk
import com.example.ui.theme.CutellyLavender
import com.example.ui.theme.CutellyMint
import java.io.File
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun KawaiiStickerView(
    sticker: StickerItem,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    isAnimated: Boolean = true,
    forcedAnimation: String? = null,
    customAnimation: CustomAnimationItem? = null,
    opacity: Float = 1.0f
) {
    val isCustom = customAnimation != null
    val animDuration = customAnimation?.durationMs ?: 1000
    val animType = try {
        StickerAnimationType.valueOf(forcedAnimation ?: sticker.defaultAnimation)
    } catch (_: Exception) {
        StickerAnimationType.IDLE_BREATHE
    }

    val infiniteTransition = rememberInfiniteTransition(label = "sticker_anim")

    // Easing selection
    val easing = if (isCustom) {
        when (customAnimation?.easingType) {
            "LINEAR" -> LinearEasing
            "BOUNCY" -> FastOutSlowInEasing
            else -> FastOutSlowInEasing
        }
    } else {
        if (animType == StickerAnimationType.ROTATION) LinearEasing else FastOutSlowInEasing
    }

    // Scale animation
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = if (!isAnimated) 1.0f else if (isCustom) customAnimation!!.minScale else when (animType) {
            StickerAnimationType.IDLE_BREATHE -> 0.95f
            StickerAnimationType.SOFT_BOUNCE -> 0.92f
            StickerAnimationType.HAPPY_JUMP -> 0.9f
            StickerAnimationType.SQUISH -> 0.9f
            else -> 1.0f
        },
        targetValue = if (!isAnimated) 1.0f else if (isCustom) customAnimation!!.maxScale else when (animType) {
            StickerAnimationType.IDLE_BREATHE -> 1.06f
            StickerAnimationType.SOFT_BOUNCE -> 1.08f
            StickerAnimationType.HAPPY_JUMP -> 1.14f
            StickerAnimationType.SQUISH -> 1.1f
            else -> 1.0f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isCustom) animDuration else when (animType) {
                    StickerAnimationType.IDLE_BREATHE -> 1400
                    StickerAnimationType.SOFT_BOUNCE -> 600
                    StickerAnimationType.HAPPY_JUMP -> 500
                    StickerAnimationType.SQUISH -> 700
                    else -> 1000
                },
                easing = easing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Float translation Y animation
    val offsetYAnim by infiniteTransition.animateFloat(
        initialValue = if (!isAnimated) 0f else if (isCustom) -customAnimation!!.translateYDp else when (animType) {
            StickerAnimationType.GENTLE_FLOAT -> -6f
            StickerAnimationType.HAPPY_JUMP -> 6f
            else -> 0f
        },
        targetValue = if (!isAnimated) 0f else if (isCustom) customAnimation!!.translateYDp else when (animType) {
            StickerAnimationType.GENTLE_FLOAT -> 6f
            StickerAnimationType.HAPPY_JUMP -> -14f
            else -> 0f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isCustom) animDuration else if (animType == StickerAnimationType.HAPPY_JUMP) 450 else 1600,
                easing = easing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    // Translation X animation (for sways & jitters)
    val offsetXAnim by infiniteTransition.animateFloat(
        initialValue = if (!isAnimated || !isCustom) 0f else -customAnimation!!.translateXDp,
        targetValue = if (!isAnimated || !isCustom) 0f else customAnimation!!.translateXDp,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isCustom) animDuration else 1000,
                easing = easing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetX"
    )

    // Rotation animation
    val isContinuousSpin = isCustom && customAnimation!!.baseType == "SPIN" && customAnimation.rotationAngleDeg >= 350f
    val rotationAnim by infiniteTransition.animateFloat(
        initialValue = if (!isAnimated) 0f else if (isCustom) {
            if (isContinuousSpin) 0f else -customAnimation!!.rotationAngleDeg
        } else when (animType) {
            StickerAnimationType.WIGGLE -> -8f
            StickerAnimationType.WAVE -> -12f
            StickerAnimationType.ROTATION -> 0f
            else -> 0f
        },
        targetValue = if (!isAnimated) 0f else if (isCustom) {
            if (isContinuousSpin) 360f else customAnimation!!.rotationAngleDeg
        } else when (animType) {
            StickerAnimationType.WIGGLE -> 8f
            StickerAnimationType.WAVE -> 12f
            StickerAnimationType.ROTATION -> 360f
            else -> 0f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isCustom) animDuration else when (animType) {
                    StickerAnimationType.WIGGLE -> 350
                    StickerAnimationType.WAVE -> 550
                    StickerAnimationType.ROTATION -> 3000
                    else -> 1000
                },
                easing = if (isContinuousSpin) LinearEasing else easing
            ),
            repeatMode = if (isContinuousSpin || (!isCustom && animType == StickerAnimationType.ROTATION)) RepeatMode.Restart else RepeatMode.Reverse
        ),
        label = "rotation"
    )

    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isAnimated && !isCustom && animType == StickerAnimationType.BLINKING) 0.35f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink"
    )

    Box(
        modifier = modifier
            .size(size)
            .alpha(opacity * (if (!isCustom && animType == StickerAnimationType.BLINKING) blinkAlpha else 1.0f))
            .offset(x = offsetXAnim.dp, y = offsetYAnim.dp)
            .scale(scaleAnim)
            .rotate(rotationAnim),
        contentAlignment = Alignment.Center
    ) {
        if (sticker.iconType == "custom_image" && !sticker.customImagePath.isNullOrEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(File(sticker.customImagePath))
                    .crossfade(true)
                    .build(),
                contentDescription = sticker.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Fit
            )
        } else if (sticker.iconType == "custom_text") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .background(Color(android.graphics.Color.parseColor(sticker.primaryColorHex)), RoundedCornerShape(18.dp))
                    .border(2.dp, Color(android.graphics.Color.parseColor(sticker.secondaryColorHex)), RoundedCornerShape(18.dp))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = sticker.customText.ifEmpty { ":3" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CutellyInk,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Native procedural Kawaii Vector Art
            Canvas(modifier = Modifier.fillMaxSize().padding(2.dp)) {
                drawKawaiiCharacter(sticker.id, sticker.primaryColorHex, sticker.secondaryColorHex)
            }
        }

        // Particle Overlay (Supports Custom Animation Particles & Built-ins)
        val particleType = if (isCustom) customAnimation!!.particleType else when (animType) {
            StickerAnimationType.HEART_BURST -> "hearts"
            StickerAnimationType.SPARKLE -> "sparkles"
            StickerAnimationType.SLEEP -> "zzz"
            else -> "none"
        }

        if (isAnimated && particleType != "none") {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val p = ((offsetYAnim + 12f) / 24f).coerceIn(0f, 1f)
                val w = this.size.width
                val h = this.size.height

                when (particleType) {
                    "hearts" -> {
                        drawHeart(center = Offset(w * 0.85f, h * (0.35f - p * 0.25f)), heartSize = 10.dp.toPx(), fillColor = CutellyBlush)
                        drawHeart(center = Offset(w * 0.15f, h * (0.45f - (1f - p) * 0.25f)), heartSize = 8.dp.toPx(), fillColor = Color(0xFFFFB2CE))
                        drawHeart(center = Offset(w * 0.5f, h * (0.15f - p * 0.12f)), heartSize = 6.dp.toPx(), fillColor = CutellyBlush.copy(alpha = 0.8f))
                    }
                    "sparkles" -> {
                        drawFourPointStar(center = Offset(w * 0.85f, h * 0.2f), starSize = 9.dp.toPx(), color = Color(0xFFFFE066))
                        drawFourPointStar(center = Offset(w * 0.15f, h * 0.3f), starSize = 7.dp.toPx(), color = Color(0xFFFF78AB))
                        drawFourPointStar(center = Offset(w * 0.75f, h * 0.75f), starSize = 6.dp.toPx(), color = Color(0xFF6EDFA3))
                    }
                    "stars" -> {
                        drawFourPointStar(center = Offset(w * 0.88f, h * (0.25f + p * 0.1f)), starSize = 10.dp.toPx(), color = Color(0xFFFFD464))
                        drawFourPointStar(center = Offset(w * 0.12f, h * (0.35f - p * 0.1f)), starSize = 8.dp.toPx(), color = Color(0xFFBCA6FF))
                        drawFourPointStar(center = Offset(w * 0.8f, h * 0.78f), starSize = 7.dp.toPx(), color = Color(0xFFFF78AB))
                    }
                    "bubbles" -> {
                        drawKawaiiBubble(center = Offset(w * 0.82f, h * (0.3f - p * 0.2f)), radius = 7.dp.toPx(), color = CutellyMint)
                        drawKawaiiBubble(center = Offset(w * 0.16f, h * (0.4f - (1f - p) * 0.2f)), radius = 5.5.dp.toPx(), color = CutellyLavender)
                        drawKawaiiBubble(center = Offset(w * 0.75f, h * (0.75f - p * 0.15f)), radius = 4.dp.toPx(), color = CutellyBlush)
                    }
                    "notes" -> {
                        drawMusicNote(center = Offset(w * 0.84f, h * (0.3f - p * 0.25f)), noteSize = 12.dp.toPx(), color = CutellyLavender)
                        drawMusicNote(center = Offset(w * 0.14f, h * (0.4f - (1f - p) * 0.25f)), noteSize = 10.dp.toPx(), color = CutellyBlush)
                    }
                    "zzz" -> {
                        drawSleepZzz(center = Offset(w * 0.82f, h * (0.32f - p * 0.2f)), size = 11.dp.toPx(), color = Color(0xFF9D7BFF))
                        drawSleepZzz(center = Offset(w * 0.92f, h * (0.16f - p * 0.2f)), size = 8.dp.toPx(), color = Color(0xFFBCA6FF))
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawKawaiiCharacter(id: String, primaryHex: String, secondaryHex: String) {
    val primaryColor = try { Color(android.graphics.Color.parseColor(primaryHex)) } catch (_: Exception) { Color.White }
    val secondaryColor = try { Color(android.graphics.Color.parseColor(secondaryHex)) } catch (_: Exception) { CutellyBlush }
    val strokeColor = CutellyInk
    val stroke = Stroke(width = 2.4.dp.toPx())

    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f

    when {
        id.startsWith("cat_") || id == "cat_white" || id == "cat_black" -> {
            drawCuteCat(cx, cy, w, h, primaryColor, secondaryColor, strokeColor, stroke, id)
        }
        id.startsWith("bunny_") -> {
            drawCuteBunny(cx, cy, w, h, primaryColor, secondaryColor, strokeColor, stroke)
        }
        id.startsWith("bear_") || id == "sleepy_bear" -> {
            drawCuteBear(cx, cy, w, h, primaryColor, secondaryColor, strokeColor, stroke)
        }
        id.startsWith("panda") -> {
            drawCutePanda(cx, cy, w, h, strokeColor, stroke)
        }
        id.startsWith("frog_") -> {
            drawCuteFrog(cx, cy, w, h, primaryColor, strokeColor, stroke)
        }
        id.startsWith("ghost_") -> {
            drawCuteGhost(cx, cy, w, h, strokeColor, stroke)
        }
        id == "penguin" -> {
            drawCutePenguin(cx, cy, w, h, strokeColor, stroke)
        }
        id == "dino" -> {
            drawCuteDino(cx, cy, w, h, primaryColor, strokeColor, stroke)
        }
        id == "strawberry" -> {
            drawCuteStrawberry(cx, cy, w, h, strokeColor, stroke)
        }
        id == "cherries" -> {
            drawCuteCherries(cx, cy, w, h, strokeColor, stroke)
        }
        id == "toast" -> {
            drawCuteToast(cx, cy, w, h, strokeColor, stroke)
        }
        id == "iced_drink" -> {
            drawCuteBoba(cx, cy, w, h, strokeColor, stroke)
        }
        id.startsWith("star_") || id == "pixel_star" -> {
            drawCuteStar(cx, cy, w * 0.42f, primaryColor, strokeColor, stroke)
        }
        id.startsWith("heart_") || id == "pixel_heart" -> {
            drawHeart(Offset(cx, cy), w * 0.44f, secondaryColor, strokeColor, stroke)
        }
        id.startsWith("cloud_") -> {
            drawCuteCloud(cx, cy, w, h, strokeColor, stroke)
        }
        id == "flower_pink" || id == "tulip" -> {
            drawCuteFlower(cx, cy, w * 0.38f, secondaryColor, strokeColor, stroke)
        }
        id == "saturn" -> {
            drawCutePlanet(cx, cy, w, h, primaryColor, secondaryColor, strokeColor, stroke)
        }
        id == "ribbon_bow" -> {
            drawCuteBow(cx, cy, w, h, secondaryColor, strokeColor, stroke)
        }
        else -> {
            // Generic cute round creature
            drawCuteDefaultMascot(cx, cy, w, h, primaryColor, secondaryColor, strokeColor, stroke)
        }
    }
}

private fun DrawScope.drawCuteCat(
    cx: Float, cy: Float, w: Float, h: Float,
    bodyColor: Color, blushColor: Color, strokeColor: Color, stroke: Stroke, id: String
) {
    val r = w * 0.35f
    // Cat ears
    val earLeft = Path().apply {
        moveTo(cx - r * 0.75f, cy - r * 0.45f)
        lineTo(cx - r * 0.85f, cy - r * 1.15f)
        lineTo(cx - r * 0.2f, cy - r * 0.8f)
        close()
    }
    val earRight = Path().apply {
        moveTo(cx + r * 0.75f, cy - r * 0.45f)
        lineTo(cx + r * 0.85f, cy - r * 1.15f)
        lineTo(cx + r * 0.2f, cy - r * 0.8f)
        close()
    }

    drawPath(earLeft, bodyColor)
    drawPath(earLeft, strokeColor, style = stroke)
    drawPath(earRight, bodyColor)
    drawPath(earRight, strokeColor, style = stroke)

    // Inner ear pink
    val innerLeft = Path().apply {
        moveTo(cx - r * 0.65f, cy - r * 0.5f)
        lineTo(cx - r * 0.78f, cy - r * 1.0f)
        lineTo(cx - r * 0.3f, cy - r * 0.75f)
        close()
    }
    val innerRight = Path().apply {
        moveTo(cx + r * 0.65f, cy - r * 0.5f)
        lineTo(cx + r * 0.78f, cy - r * 1.0f)
        lineTo(cx + r * 0.3f, cy - r * 0.75f)
        close()
    }
    drawPath(innerLeft, blushColor)
    drawPath(innerRight, blushColor)

    // Face round
    drawCircle(bodyColor, radius = r, center = Offset(cx, cy))
    drawCircle(strokeColor, radius = r, center = Offset(cx, cy), style = stroke)

    // Rosy Cheeks
    drawCircle(blushColor.copy(alpha = 0.8f), radius = r * 0.22f, center = Offset(cx - r * 0.55f, cy + r * 0.2f))
    drawCircle(blushColor.copy(alpha = 0.8f), radius = r * 0.22f, center = Offset(cx + r * 0.55f, cy + r * 0.2f))

    // Eyes
    if (id == "cat_sleepy") {
        // Closed happy eyes ^ ^
        drawArc(strokeColor, startAngle = 180f, sweepAngle = 180f, useCenter = false,
            topLeft = Offset(cx - r * 0.45f, cy - r * 0.1f), size = Size(r * 0.28f, r * 0.2f), style = stroke)
        drawArc(strokeColor, startAngle = 180f, sweepAngle = 180f, useCenter = false,
            topLeft = Offset(cx + r * 0.17f, cy - r * 0.1f), size = Size(r * 0.28f, r * 0.2f), style = stroke)
    } else {
        // Big cute eyes with shine
        val eyeColor = if (id == "cat_black") Color.White else strokeColor
        drawCircle(eyeColor, radius = r * 0.12f, center = Offset(cx - r * 0.3f, cy))
        drawCircle(eyeColor, radius = r * 0.12f, center = Offset(cx + r * 0.3f, cy))
        drawCircle(Color.White, radius = r * 0.05f, center = Offset(cx - r * 0.32f, cy - r * 0.04f))
        drawCircle(Color.White, radius = r * 0.05f, center = Offset(cx + r * 0.28f, cy - r * 0.04f))
    }

    // Mouth :3
    val mouth = Path().apply {
        moveTo(cx - r * 0.18f, cy + r * 0.12f)
        quadraticTo(cx - r * 0.09f, cy + r * 0.24f, cx, cy + r * 0.14f)
        quadraticTo(cx + r * 0.09f, cy + r * 0.24f, cx + r * 0.18f, cy + r * 0.12f)
    }
    drawPath(mouth, strokeColor, style = stroke)

    // Nose
    drawCircle(blushColor, radius = r * 0.06f, center = Offset(cx, cy + r * 0.08f))
}

private fun DrawScope.drawCuteBunny(
    cx: Float, cy: Float, w: Float, h: Float,
    bodyColor: Color, blushColor: Color, strokeColor: Color, stroke: Stroke
) {
    val r = w * 0.32f
    // Long cute bunny ears
    val earLeft = Path().apply {
        moveTo(cx - r * 0.6f, cy - r * 0.3f)
        cubicTo(cx - r * 1.1f, cy - r * 1.6f, cx - r * 0.2f, cy - r * 1.6f, cx - r * 0.1f, cy - r * 0.7f)
        close()
    }
    val earRight = Path().apply {
        moveTo(cx + r * 0.1f, cy - r * 0.7f)
        cubicTo(cx + r * 0.2f, cy - r * 1.6f, cx + r * 1.1f, cy - r * 1.6f, cx + r * 0.6f, cy - r * 0.3f)
        close()
    }
    drawPath(earLeft, bodyColor)
    drawPath(earLeft, strokeColor, style = stroke)
    drawPath(earRight, bodyColor)
    drawPath(earRight, strokeColor, style = stroke)

    // Inner pink ears
    drawCircle(blushColor, radius = r * 0.14f, center = Offset(cx - r * 0.45f, cy - r * 1.0f))
    drawCircle(blushColor, radius = r * 0.14f, center = Offset(cx + r * 0.45f, cy - r * 1.0f))

    // Head
    drawCircle(bodyColor, radius = r, center = Offset(cx, cy + r * 0.2f))
    drawCircle(strokeColor, radius = r, center = Offset(cx, cy + r * 0.2f), style = stroke)

    // Rosy cheeks & eyes
    drawCircle(blushColor.copy(alpha = 0.8f), radius = r * 0.2f, center = Offset(cx - r * 0.5f, cy + r * 0.35f))
    drawCircle(blushColor.copy(alpha = 0.8f), radius = r * 0.2f, center = Offset(cx + r * 0.5f, cy + r * 0.35f))
    drawCircle(strokeColor, radius = r * 0.11f, center = Offset(cx - r * 0.28f, cy + r * 0.18f))
    drawCircle(strokeColor, radius = r * 0.11f, center = Offset(cx + r * 0.28f, cy + r * 0.18f))
    drawCircle(Color.White, radius = r * 0.04f, center = Offset(cx - r * 0.3f, cy + r * 0.15f))
    drawCircle(Color.White, radius = r * 0.04f, center = Offset(cx + r * 0.26f, cy + r * 0.15f))

    // Nose
    drawCircle(blushColor, radius = r * 0.06f, center = Offset(cx, cy + r * 0.26f))
}

private fun DrawScope.drawCuteBear(
    cx: Float, cy: Float, w: Float, h: Float,
    bodyColor: Color, blushColor: Color, strokeColor: Color, stroke: Stroke
) {
    val r = w * 0.34f
    // Round bear ears
    drawCircle(bodyColor, radius = r * 0.4f, center = Offset(cx - r * 0.7f, cy - r * 0.65f))
    drawCircle(strokeColor, radius = r * 0.4f, center = Offset(cx - r * 0.7f, cy - r * 0.65f), style = stroke)
    drawCircle(bodyColor, radius = r * 0.4f, center = Offset(cx + r * 0.7f, cy - r * 0.65f))
    drawCircle(strokeColor, radius = r * 0.4f, center = Offset(cx + r * 0.7f, cy - r * 0.65f), style = stroke)
    drawCircle(blushColor, radius = r * 0.2f, center = Offset(cx - r * 0.7f, cy - r * 0.65f))
    drawCircle(blushColor, radius = r * 0.2f, center = Offset(cx + r * 0.7f, cy - r * 0.65f))

    // Head
    drawCircle(bodyColor, radius = r, center = Offset(cx, cy))
    drawCircle(strokeColor, radius = r, center = Offset(cx, cy), style = stroke)

    // Snout
    drawRoundRect(blushColor, topLeft = Offset(cx - r * 0.36f, cy + r * 0.02f), size = Size(r * 0.72f, r * 0.5f), cornerRadius = CornerRadius(r * 0.25f))
    drawCircle(strokeColor, radius = r * 0.1f, center = Offset(cx, cy + r * 0.16f))

    // Eyes
    drawCircle(strokeColor, radius = r * 0.11f, center = Offset(cx - r * 0.35f, cy - r * 0.05f))
    drawCircle(strokeColor, radius = r * 0.11f, center = Offset(cx + r * 0.35f, cy - r * 0.05f))
}

private fun DrawScope.drawCutePanda(
    cx: Float, cy: Float, w: Float, h: Float,
    strokeColor: Color, stroke: Stroke
) {
    val r = w * 0.34f
    // Black panda ears
    drawCircle(strokeColor, radius = r * 0.38f, center = Offset(cx - r * 0.72f, cy - r * 0.65f))
    drawCircle(strokeColor, radius = r * 0.38f, center = Offset(cx + r * 0.72f, cy - r * 0.65f))

    // White face
    drawCircle(Color.White, radius = r, center = Offset(cx, cy))
    drawCircle(strokeColor, radius = r, center = Offset(cx, cy), style = stroke)

    // Big black eye patches
    drawRoundRect(strokeColor, topLeft = Offset(cx - r * 0.55f, cy - r * 0.2f), size = Size(r * 0.38f, r * 0.45f), cornerRadius = CornerRadius(r * 0.18f))
    drawRoundRect(strokeColor, topLeft = Offset(cx + r * 0.17f, cy - r * 0.2f), size = Size(r * 0.38f, r * 0.45f), cornerRadius = CornerRadius(r * 0.18f))

    // White eye dots
    drawCircle(Color.White, radius = r * 0.09f, center = Offset(cx - r * 0.35f, cy))
    drawCircle(Color.White, radius = r * 0.09f, center = Offset(cx + r * 0.35f, cy))

    // Nose
    drawCircle(strokeColor, radius = r * 0.08f, center = Offset(cx, cy + r * 0.18f))
    // Pink cheeks
    drawCircle(CutellyBlush.copy(alpha = 0.7f), radius = r * 0.18f, center = Offset(cx - r * 0.6f, cy + r * 0.25f))
    drawCircle(CutellyBlush.copy(alpha = 0.7f), radius = r * 0.18f, center = Offset(cx + r * 0.6f, cy + r * 0.25f))
}

private fun DrawScope.drawCuteFrog(
    cx: Float, cy: Float, w: Float, h: Float,
    frogGreen: Color, strokeColor: Color, stroke: Stroke
) {
    val r = w * 0.33f
    // Frog eye bumps
    drawCircle(frogGreen, radius = r * 0.42f, center = Offset(cx - r * 0.55f, cy - r * 0.6f))
    drawCircle(strokeColor, radius = r * 0.42f, center = Offset(cx - r * 0.55f, cy - r * 0.6f), style = stroke)
    drawCircle(frogGreen, radius = r * 0.42f, center = Offset(cx + r * 0.55f, cy - r * 0.6f))
    drawCircle(strokeColor, radius = r * 0.42f, center = Offset(cx + r * 0.55f, cy - r * 0.6f), style = stroke)

    // Frog head
    drawRoundRect(frogGreen, topLeft = Offset(cx - r * 1.05f, cy - r * 0.4f), size = Size(r * 2.1f, r * 1.6f), cornerRadius = CornerRadius(r * 0.8f))
    drawRoundRect(strokeColor, topLeft = Offset(cx - r * 1.05f, cy - r * 0.4f), size = Size(r * 2.1f, r * 1.6f), cornerRadius = CornerRadius(r * 0.8f), style = stroke)

    // Big frog eyes
    drawCircle(strokeColor, radius = r * 0.16f, center = Offset(cx - r * 0.55f, cy - r * 0.6f))
    drawCircle(Color.White, radius = r * 0.07f, center = Offset(cx - r * 0.58f, cy - r * 0.65f))
    drawCircle(strokeColor, radius = r * 0.16f, center = Offset(cx + r * 0.55f, cy - r * 0.6f))
    drawCircle(Color.White, radius = r * 0.07f, center = Offset(cx + r * 0.52f, cy - r * 0.65f))

    // Rosy Cheeks
    drawCircle(CutellyBlush, radius = r * 0.22f, center = Offset(cx - r * 0.65f, cy + r * 0.18f))
    drawCircle(CutellyBlush, radius = r * 0.22f, center = Offset(cx + r * 0.65f, cy + r * 0.18f))

    // Wide happy smile
    val smile = Path().apply {
        moveTo(cx - r * 0.4f, cy + r * 0.1f)
        quadraticTo(cx, cy + r * 0.45f, cx + r * 0.4f, cy + r * 0.1f)
    }
    drawPath(smile, strokeColor, style = stroke)
}

private fun DrawScope.drawCuteGhost(
    cx: Float, cy: Float, w: Float, h: Float,
    strokeColor: Color, stroke: Stroke
) {
    val r = w * 0.34f
    val ghost = Path().apply {
        moveTo(cx - r, cy)
        cubicTo(cx - r, cy - r * 1.2f, cx + r, cy - r * 1.2f, cx + r, cy)
        lineTo(cx + r, cy + r * 0.9f)
        // wavy ripples
        quadraticTo(cx + r * 0.66f, cy + r * 0.6f, cx + r * 0.33f, cy + r * 0.9f)
        quadraticTo(cx, cy + r * 0.6f, cx - r * 0.33f, cy + r * 0.9f)
        quadraticTo(cx - r * 0.66f, cy + r * 0.6f, cx - r, cy + r * 0.9f)
        close()
    }
    drawPath(ghost, Color.White)
    drawPath(ghost, strokeColor, style = stroke)

    // Eyes
    drawCircle(strokeColor, radius = r * 0.13f, center = Offset(cx - r * 0.35f, cy - r * 0.15f))
    drawCircle(strokeColor, radius = r * 0.13f, center = Offset(cx + r * 0.35f, cy - r * 0.15f))
    // Cheeks
    drawCircle(CutellyBlush, radius = r * 0.2f, center = Offset(cx - r * 0.5f, cy + r * 0.08f))
    drawCircle(CutellyBlush, radius = r * 0.2f, center = Offset(cx + r * 0.5f, cy + r * 0.08f))
    // Tiny mouth
    drawCircle(strokeColor, radius = r * 0.08f, center = Offset(cx, cy + r * 0.02f))
}

private fun DrawScope.drawCutePenguin(
    cx: Float, cy: Float, w: Float, h: Float,
    strokeColor: Color, stroke: Stroke
) {
    val r = w * 0.34f
    // Black outer body
    drawRoundRect(strokeColor, topLeft = Offset(cx - r, cy - r * 0.9f), size = Size(r * 2f, r * 2f), cornerRadius = CornerRadius(r * 0.95f))
    // White inner belly/face
    drawRoundRect(Color.White, topLeft = Offset(cx - r * 0.7f, cy - r * 0.6f), size = Size(r * 1.4f, r * 1.6f), cornerRadius = CornerRadius(r * 0.7f))

    // Eyes
    drawCircle(strokeColor, radius = r * 0.1f, center = Offset(cx - r * 0.32f, cy - r * 0.15f))
    drawCircle(strokeColor, radius = r * 0.1f, center = Offset(cx + r * 0.32f, cy - r * 0.15f))
    // Orange beak
    val beak = Path().apply {
        moveTo(cx - r * 0.16f, cy - r * 0.05f)
        lineTo(cx + r * 0.16f, cy - r * 0.05f)
        lineTo(cx, cy + r * 0.14f)
        close()
    }
    drawPath(beak, Color(0xFFFF9E40))
    // Cheeks
    drawCircle(CutellyBlush, radius = r * 0.18f, center = Offset(cx - r * 0.5f, cy - r * 0.02f))
    drawCircle(CutellyBlush, radius = r * 0.18f, center = Offset(cx + r * 0.5f, cy - r * 0.02f))
}

private fun DrawScope.drawCuteDino(
    cx: Float, cy: Float, w: Float, h: Float,
    dinoGreen: Color, strokeColor: Color, stroke: Stroke
) {
    val r = w * 0.32f
    // Tiny spikes on back
    drawCircle(Color(0xFFFFB2CE), radius = r * 0.2f, center = Offset(cx - r * 0.7f, cy - r * 0.4f))
    drawCircle(Color(0xFFFFB2CE), radius = r * 0.2f, center = Offset(cx - r * 0.75f, cy))
    drawCircle(Color(0xFFFFB2CE), radius = r * 0.2f, center = Offset(cx - r * 0.7f, cy + r * 0.4f))

    // Body
    drawRoundRect(dinoGreen, topLeft = Offset(cx - r * 0.8f, cy - r * 0.8f), size = Size(r * 1.7f, r * 1.7f), cornerRadius = CornerRadius(r * 0.6f))
    drawRoundRect(strokeColor, topLeft = Offset(cx - r * 0.8f, cy - r * 0.8f), size = Size(r * 1.7f, r * 1.7f), cornerRadius = CornerRadius(r * 0.6f), style = stroke)

    // Eye
    drawCircle(strokeColor, radius = r * 0.12f, center = Offset(cx + r * 0.35f, cy - r * 0.25f))
    drawCircle(Color.White, radius = r * 0.05f, center = Offset(cx + r * 0.32f, cy - r * 0.28f))
    // Cheek
    drawCircle(CutellyBlush, radius = r * 0.2f, center = Offset(cx + r * 0.25f, cy))
    // Smile
    val smile = Path().apply {
        moveTo(cx + r * 0.35f, cy + r * 0.12f)
        quadraticTo(cx + r * 0.5f, cy + r * 0.2f, cx + r * 0.6f, cy + r * 0.12f)
    }
    drawPath(smile, strokeColor, style = stroke)
}

private fun DrawScope.drawCuteStrawberry(
    cx: Float, cy: Float, w: Float, h: Float,
    strokeColor: Color, stroke: Stroke
) {
    val r = w * 0.35f
    val berry = Path().apply {
        moveTo(cx, cy + r * 1.05f)
        cubicTo(cx - r * 1.1f, cy + r * 0.4f, cx - r * 0.9f, cy - r * 0.5f, cx, cy - r * 0.6f)
        cubicTo(cx + r * 0.9f, cy - r * 0.5f, cx + r * 1.1f, cy + r * 0.4f, cx, cy + r * 1.05f)
        close()
    }
    drawPath(berry, Color(0xFFFF5277))
    drawPath(berry, strokeColor, style = stroke)

    // Green leaves on top
    val leaf = Path().apply {
        moveTo(cx, cy - r * 0.55f)
        lineTo(cx - r * 0.5f, cy - r * 0.85f)
        lineTo(cx - r * 0.2f, cy - r * 0.55f)
        lineTo(cx, cy - r * 0.95f)
        lineTo(cx + r * 0.2f, cy - r * 0.55f)
        lineTo(cx + r * 0.5f, cy - r * 0.85f)
        close()
    }
    drawPath(leaf, Color(0xFF6EDFA3))
    drawPath(leaf, strokeColor, style = stroke)

    // Seeds / face
    drawCircle(Color.White, radius = r * 0.08f, center = Offset(cx - r * 0.25f, cy))
    drawCircle(Color.White, radius = r * 0.08f, center = Offset(cx + r * 0.25f, cy))
    drawCircle(strokeColor, radius = r * 0.07f, center = Offset(cx - r * 0.25f, cy))
    drawCircle(strokeColor, radius = r * 0.07f, center = Offset(cx + r * 0.25f, cy))
    drawCircle(Color(0xFFFFB2CE), radius = r * 0.15f, center = Offset(cx - r * 0.45f, cy + r * 0.15f))
    drawCircle(Color(0xFFFFB2CE), radius = r * 0.15f, center = Offset(cx + r * 0.45f, cy + r * 0.15f))
}

private fun DrawScope.drawCuteCherries(
    cx: Float, cy: Float, w: Float, h: Float,
    strokeColor: Color, stroke: Stroke
) {
    val r = w * 0.22f
    // Two cherries
    val c1 = Offset(cx - r * 0.9f, cy + r * 0.6f)
    val c2 = Offset(cx + r * 0.8f, cy + r * 0.4f)
    val stemTop = Offset(cx + r * 0.2f, cy - r * 1.1f)

    // Stems
    val stem1 = Path().apply {
        moveTo(c1.x, c1.y - r * 0.6f)
        quadraticTo(cx - r * 0.5f, cy - r * 0.4f, stemTop.x, stemTop.y)
    }
    val stem2 = Path().apply {
        moveTo(c2.x, c2.y - r * 0.6f)
        quadraticTo(cx + r * 0.6f, cy - r * 0.4f, stemTop.x, stemTop.y)
    }
    drawPath(stem1, Color(0xFF48A873), style = Stroke(width = 3.dp.toPx()))
    drawPath(stem2, Color(0xFF48A873), style = Stroke(width = 3.dp.toPx()))

    // Leaf
    drawOval(Color(0xFF6EDFA3), topLeft = Offset(stemTop.x - r * 0.5f, stemTop.y - r * 0.3f), size = Size(r * 0.9f, r * 0.5f))

    // Cherry circles
    drawCircle(Color(0xFFFF3366), radius = r, center = c1)
    drawCircle(strokeColor, radius = r, center = c1, style = stroke)
    drawCircle(Color(0xFFFF3366), radius = r, center = c2)
    drawCircle(strokeColor, radius = r, center = c2, style = stroke)

    // Highlights
    drawCircle(Color.White, radius = r * 0.25f, center = Offset(c1.x - r * 0.3f, c1.y - r * 0.3f))
    drawCircle(Color.White, radius = r * 0.25f, center = Offset(c2.x - r * 0.3f, c2.y - r * 0.3f))
}

private fun DrawScope.drawCuteToast(
    cx: Float, cy: Float, w: Float, h: Float,
    strokeColor: Color, stroke: Stroke
) {
    val r = w * 0.34f
    val toast = Path().apply {
        moveTo(cx - r * 0.8f, cy + r * 0.9f)
        lineTo(cx + r * 0.8f, cy + r * 0.9f)
        cubicTo(cx + r * 0.95f, cy + r * 0.9f, cx + r * 0.95f, cy - r * 0.4f, cx + r * 0.7f, cy - r * 0.5f)
        cubicTo(cx + r * 0.6f, cy - r * 0.95f, cx + r * 0.1f, cy - r * 0.95f, cx, cy - r * 0.65f)
        cubicTo(cx - r * 0.1f, cy - r * 0.95f, cx - r * 0.6f, cy - r * 0.95f, cx - r * 0.7f, cy - r * 0.5f)
        cubicTo(cx - r * 0.95f, cy - r * 0.4f, cx - r * 0.95f, cy + r * 0.9f, cx - r * 0.8f, cy + r * 0.9f)
        close()
    }
    drawPath(toast, Color(0xFFF7D29E))
    drawPath(toast, strokeColor, style = stroke)

    // Butter square
    drawRoundRect(Color(0xFFFFEE70), topLeft = Offset(cx - r * 0.25f, cy - r * 0.25f), size = Size(r * 0.5f, r * 0.4f), cornerRadius = CornerRadius(r * 0.1f))

    // Eyes & Smile
    drawCircle(strokeColor, radius = r * 0.1f, center = Offset(cx - r * 0.4f, cy + r * 0.25f))
    drawCircle(strokeColor, radius = r * 0.1f, center = Offset(cx + r * 0.4f, cy + r * 0.25f))
    drawCircle(CutellyBlush, radius = r * 0.18f, center = Offset(cx - r * 0.55f, cy + r * 0.4f))
    drawCircle(CutellyBlush, radius = r * 0.18f, center = Offset(cx + r * 0.55f, cy + r * 0.4f))
}

private fun DrawScope.drawCuteBoba(
    cx: Float, cy: Float, w: Float, h: Float,
    strokeColor: Color, stroke: Stroke
) {
    val r = w * 0.3f
    // Cup
    val cup = Path().apply {
        moveTo(cx - r * 0.8f, cy - r * 0.7f)
        lineTo(cx - r * 0.6f, cy + r * 0.9f)
        quadraticTo(cx, cy + r * 1.1f, cx + r * 0.6f, cy + r * 0.9f)
        lineTo(cx + r * 0.8f, cy - r * 0.7f)
        close()
    }
    drawPath(cup, Color(0xFFF2D1B3))
    drawPath(cup, strokeColor, style = stroke)

    // Dome lid
    drawArc(Color(0xFFE8F4FC), startAngle = 180f, sweepAngle = 180f, useCenter = true,
        topLeft = Offset(cx - r * 0.85f, cy - r * 1.15f), size = Size(r * 1.7f, r * 0.9f))
    drawArc(strokeColor, startAngle = 180f, sweepAngle = 180f, useCenter = false,
        topLeft = Offset(cx - r * 0.85f, cy - r * 1.15f), size = Size(r * 1.7f, r * 0.9f), style = stroke)

    // Straw
    val straw = Path().apply {
        moveTo(cx + r * 0.1f, cy - r * 1.4f)
        lineTo(cx + r * 0.25f, cy - r * 1.4f)
        lineTo(cx + r * 0.15f, cy - r * 0.6f)
        lineTo(cx, cy - r * 0.6f)
        close()
    }
    drawPath(straw, Color(0xFFFF78AB))

    // Boba pearls
    drawCircle(strokeColor, radius = r * 0.14f, center = Offset(cx - r * 0.3f, cy + r * 0.7f))
    drawCircle(strokeColor, radius = r * 0.14f, center = Offset(cx + r * 0.28f, cy + r * 0.75f))
    drawCircle(strokeColor, radius = r * 0.14f, center = Offset(cx, cy + r * 0.6f))
}

private fun DrawScope.drawCuteStar(
    cx: Float, cy: Float, radius: Float,
    fillColor: Color, strokeColor: Color, stroke: Stroke
) {
    val path = Path()
    val points = 5
    val innerRadius = radius * 0.5f
    val step = PI / points

    for (i in 0 until 2 * points) {
        val r = if (i % 2 == 0) radius else innerRadius
        val angle = i * step - PI / 2.0
        val x = (cx + r * cos(angle)).toFloat()
        val y = (cy + r * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()

    drawPath(path, fillColor)
    drawPath(path, strokeColor, style = stroke)

    // Cute face
    drawCircle(strokeColor, radius = radius * 0.13f, center = Offset(cx - radius * 0.28f, cy))
    drawCircle(strokeColor, radius = radius * 0.13f, center = Offset(cx + radius * 0.28f, cy))
    drawCircle(CutellyBlush, radius = radius * 0.22f, center = Offset(cx - radius * 0.42f, cy + radius * 0.18f))
    drawCircle(CutellyBlush, radius = radius * 0.22f, center = Offset(cx + radius * 0.42f, cy + radius * 0.18f))
}

private fun DrawScope.drawHeart(
    center: Offset, heartSize: Float,
    fillColor: Color, strokeColor: Color? = null, stroke: Stroke? = null
) {
    val cx = center.x
    val cy = center.y
    val r = heartSize * 0.5f

    val path = Path().apply {
        moveTo(cx, cy + r * 0.9f)
        cubicTo(cx - r * 1.3f, cy + r * 0.2f, cx - r * 1.3f, cy - r * 0.9f, cx - r * 0.45f, cy - r * 0.9f)
        cubicTo(cx, cy - r * 0.9f, cx, cy - r * 0.4f, cx, cy - r * 0.3f)
        cubicTo(cx, cy - r * 0.4f, cx, cy - r * 0.9f, cx + r * 0.45f, cy - r * 0.9f)
        cubicTo(cx + r * 1.3f, cy - r * 0.9f, cx + r * 1.3f, cy + r * 0.2f, cx, cy + r * 0.9f)
        close()
    }
    drawPath(path, fillColor)
    if (strokeColor != null && stroke != null) {
        drawPath(path, strokeColor, style = stroke)
    }
}

private fun DrawScope.drawCuteCloud(
    cx: Float, cy: Float, w: Float, h: Float,
    strokeColor: Color, stroke: Stroke
) {
    val r = w * 0.32f
    val cloud = Path().apply {
        moveTo(cx - r * 0.9f, cy + r * 0.4f)
        cubicTo(cx - r * 1.2f, cy + r * 0.4f, cx - r * 1.2f, cy - r * 0.1f, cx - r * 0.7f, cy - r * 0.2f)
        cubicTo(cx - r * 0.8f, cy - r * 0.8f, cx - r * 0.2f, cy - r * 0.8f, cx, cy - r * 0.4f)
        cubicTo(cx + r * 0.2f, cy - r * 0.8f, cx + r * 0.8f, cy - r * 0.8f, cx + r * 0.7f, cy - r * 0.2f)
        cubicTo(cx + r * 1.2f, cy - r * 0.1f, cx + r * 1.2f, cy + r * 0.4f, cx + r * 0.9f, cy + r * 0.4f)
        close()
    }
    drawPath(cloud, Color(0xFFEFF8FF))
    drawPath(cloud, strokeColor, style = stroke)

    // Face
    drawCircle(strokeColor, radius = r * 0.11f, center = Offset(cx - r * 0.32f, cy))
    drawCircle(strokeColor, radius = r * 0.11f, center = Offset(cx + r * 0.32f, cy))
    drawCircle(CutellyBlush, radius = r * 0.22f, center = Offset(cx - r * 0.5f, cy + r * 0.15f))
    drawCircle(CutellyBlush, radius = r * 0.22f, center = Offset(cx + r * 0.5f, cy + r * 0.15f))
}

private fun DrawScope.drawCuteFlower(
    cx: Float, cy: Float, radius: Float,
    petalColor: Color, strokeColor: Color, stroke: Stroke
) {
    val petals = 5
    val step = (2 * PI / petals).toFloat()
    for (i in 0 until petals) {
        val angle = i * step
        val px = (cx + radius * 0.55f * cos(angle)).toFloat()
        val py = (cy + radius * 0.55f * sin(angle)).toFloat()
        drawCircle(petalColor, radius = radius * 0.42f, center = Offset(px, py))
        drawCircle(strokeColor, radius = radius * 0.42f, center = Offset(px, py), style = stroke)
    }
    // Center
    drawCircle(Color(0xFFFFEE70), radius = radius * 0.4f, center = Offset(cx, cy))
    drawCircle(strokeColor, radius = radius * 0.4f, center = Offset(cx, cy), style = stroke)
    drawCircle(strokeColor, radius = radius * 0.08f, center = Offset(cx - radius * 0.14f, cy))
    drawCircle(strokeColor, radius = radius * 0.08f, center = Offset(cx + radius * 0.14f, cy))
}

private fun DrawScope.drawCutePlanet(
    cx: Float, cy: Float, w: Float, h: Float,
    planetColor: Color, ringColor: Color, strokeColor: Color, stroke: Stroke
) {
    val r = w * 0.28f
    // Planet body
    drawCircle(planetColor, radius = r, center = Offset(cx, cy))
    drawCircle(strokeColor, radius = r, center = Offset(cx, cy), style = stroke)

    // Saturn Ring angled
    val ringPath = Path().apply {
        moveTo(cx - r * 1.5f, cy + r * 0.2f)
        cubicTo(cx - r * 1.5f, cy - r * 0.5f, cx + r * 1.5f, cy - r * 0.5f, cx + r * 1.5f, cy + r * 0.2f)
        cubicTo(cx + r * 1.5f, cy + r * 0.7f, cx - r * 1.5f, cy + r * 0.7f, cx - r * 1.5f, cy + r * 0.2f)
        close()
    }
    drawPath(ringPath, ringColor.copy(alpha = 0.65f))
    drawPath(ringPath, strokeColor, style = stroke)

    // Eyes
    drawCircle(strokeColor, radius = r * 0.13f, center = Offset(cx - r * 0.32f, cy))
    drawCircle(strokeColor, radius = r * 0.13f, center = Offset(cx + r * 0.32f, cy))
}

private fun DrawScope.drawCuteBow(
    cx: Float, cy: Float, w: Float, h: Float,
    bowColor: Color, strokeColor: Color, stroke: Stroke
) {
    val r = w * 0.32f
    // Left loop
    val leftLoop = Path().apply {
        moveTo(cx, cy)
        cubicTo(cx - r * 0.5f, cy - r * 0.8f, cx - r * 1.3f, cy - r * 0.6f, cx - r * 1.1f, cy)
        cubicTo(cx - r * 1.3f, cy + r * 0.6f, cx - r * 0.5f, cy + r * 0.8f, cx, cy)
        close()
    }
    // Right loop
    val rightLoop = Path().apply {
        moveTo(cx, cy)
        cubicTo(cx + r * 0.5f, cy - r * 0.8f, cx + r * 1.3f, cy - r * 0.6f, cx + r * 1.1f, cy)
        cubicTo(cx + r * 1.3f, cy + r * 0.6f, cx + r * 0.5f, cy + r * 0.8f, cx, cy)
        close()
    }
    drawPath(leftLoop, bowColor)
    drawPath(leftLoop, strokeColor, style = stroke)
    drawPath(rightLoop, bowColor)
    drawPath(rightLoop, strokeColor, style = stroke)

    // Center knot
    drawCircle(bowColor, radius = r * 0.32f, center = Offset(cx, cy))
    drawCircle(strokeColor, radius = r * 0.32f, center = Offset(cx, cy), style = stroke)
}

private fun DrawScope.drawCuteDefaultMascot(
    cx: Float, cy: Float, w: Float, h: Float,
    primaryColor: Color, secondaryColor: Color, strokeColor: Color, stroke: Stroke
) {
    val r = w * 0.35f
    drawCircle(primaryColor, radius = r, center = Offset(cx, cy))
    drawCircle(strokeColor, radius = r, center = Offset(cx, cy), style = stroke)
    drawCircle(strokeColor, radius = r * 0.12f, center = Offset(cx - r * 0.35f, cy - r * 0.05f))
    drawCircle(strokeColor, radius = r * 0.12f, center = Offset(cx + r * 0.35f, cy - r * 0.05f))
    drawCircle(secondaryColor, radius = r * 0.22f, center = Offset(cx - r * 0.5f, cy + r * 0.2f))
    drawCircle(secondaryColor, radius = r * 0.22f, center = Offset(cx + r * 0.5f, cy + r * 0.2f))
}

private fun DrawScope.drawFourPointStar(center: Offset, starSize: Float, color: Color) {
    val path = Path().apply {
        moveTo(center.x, center.y - starSize)
        quadraticTo(center.x, center.y, center.x + starSize, center.y)
        quadraticTo(center.x, center.y, center.x, center.y + starSize)
        quadraticTo(center.x, center.y, center.x - starSize, center.y)
        quadraticTo(center.x, center.y, center.x, center.y - starSize)
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.drawMusicNote(center: Offset, noteSize: Float, color: Color) {
    val headRadius = noteSize * 0.35f
    drawCircle(color, radius = headRadius, center = Offset(center.x, center.y + headRadius))
    drawLine(color, start = Offset(center.x + headRadius * 0.8f, center.y + headRadius), end = Offset(center.x + headRadius * 0.8f, center.y - noteSize * 0.7f), strokeWidth = 2.dp.toPx())
    val flag = Path().apply {
        moveTo(center.x + headRadius * 0.8f, center.y - noteSize * 0.7f)
        quadraticTo(center.x + noteSize * 0.9f, center.y - noteSize * 0.4f, center.x + headRadius * 0.8f, center.y - noteSize * 0.1f)
    }
    drawPath(flag, color, style = Stroke(width = 2.dp.toPx()))
}

private fun DrawScope.drawKawaiiBubble(center: Offset, radius: Float, color: Color) {
    drawCircle(color.copy(alpha = 0.35f), radius = radius, center = center)
    drawCircle(color.copy(alpha = 0.75f), radius = radius, center = center, style = Stroke(width = 1.5.dp.toPx()))
    drawCircle(Color.White.copy(alpha = 0.8f), radius = radius * 0.25f, center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f))
}

private fun DrawScope.drawSleepZzz(center: Offset, size: Float, color: Color) {
    val path = Path().apply {
        moveTo(center.x - size * 0.45f, center.y - size * 0.45f)
        lineTo(center.x + size * 0.45f, center.y - size * 0.45f)
        lineTo(center.x - size * 0.45f, center.y + size * 0.45f)
        lineTo(center.x + size * 0.45f, center.y + size * 0.45f)
    }
    drawPath(path, color, style = Stroke(width = 2.dp.toPx()))
}

