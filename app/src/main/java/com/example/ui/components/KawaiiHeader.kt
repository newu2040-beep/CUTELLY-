package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CutellyBlush
import com.example.ui.theme.CutellyInk
import com.example.ui.theme.CutellySoftPink

@Composable
fun CutellyWordmark(
    modifier: Modifier = Modifier,
    fontSize: Int = 26
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "CUTELL",
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = CutellyInk,
            letterSpacing = 1.sp
        )
        Box(contentAlignment = Alignment.TopEnd) {
            Text(
                text = "Y",
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = CutellyInk,
                letterSpacing = 1.sp
            )
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = CutellyBlush,
                modifier = Modifier
                    .size((fontSize * 0.42).dp)
                    .align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
fun CutellyTopBar(
    title: String? = null,
    showBack: Boolean = false,
    onBack: () -> Unit = {},
    showSettings: Boolean = false,
    onSettings: () -> Unit = {},
    showSearch: Boolean = false,
    onSearch: () -> Unit = {},
    showHelp: Boolean = false,
    onHelp: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CutellyInk
                )
            }
        } else {
            CutellyWordmark()
        }

        if (title != null) {
            Text(
                text = title,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = CutellyInk
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showSearch) {
                IconButton(onClick = onSearch, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CutellyInk
                    )
                }
            }
            if (showHelp) {
                IconButton(onClick = onHelp, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "Help",
                        tint = CutellyInk
                    )
                }
            }
            if (showSettings) {
                IconButton(onClick = onSettings, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = CutellyInk
                    )
                }
            }
        }
    }
}
