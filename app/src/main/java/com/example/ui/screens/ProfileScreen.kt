package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserProfile
import com.example.service.CutellyAccessibilityService
import com.example.ui.components.CutellyTopBar
import com.example.ui.theme.CutellyBackground
import com.example.ui.theme.CutellyBlush
import com.example.ui.theme.CutellyCardBorder
import com.example.ui.theme.CutellyInk
import com.example.ui.theme.CutellyLavender
import com.example.ui.theme.CutellyMint
import com.example.ui.theme.CutellySecondaryText
import com.example.ui.theme.CutellySoftPink
import com.example.ui.viewmodel.CutellyViewModel
import com.example.util.KawaiiHaptics
import com.example.util.KawaiiSoundManager

@Composable
fun ProfileScreen(
    viewModel: CutellyViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val userProfiles by viewModel.userProfiles.collectAsState()
    val currentProfile by viewModel.currentProfile.collectAsState()
    val isOverlayActive by viewModel.isOverlayActive.collectAsState()

    var showBackupDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var backupJsonText by remember { mutableStateOf("") }
    var importInputText by remember { mutableStateOf("") }

    val hasOverlayPerm = Settings.canDrawOverlays(context)
    val hasAccessibility = CutellyAccessibilityService.isServiceRunning

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
                title = "Profiles & Settings",
                showBack = true,
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profiles Section
                item {
                    Text(
                        text = "Active Profile",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = CutellyInk
                    )
                }

                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(userProfiles) { profile ->
                            val isCurrent = profile.id == currentProfile?.id
                            Card(
                                modifier = Modifier
                                    .size(105.dp)
                                    .clickable {
                                        viewModel.switchProfile(profile)
                                    },
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCurrent) CutellySoftPink else Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                border = if (isCurrent) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CutellyBlush)) else null
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = getProfileIcon(profile.iconName),
                                        contentDescription = profile.name,
                                        tint = if (isCurrent) CutellyBlush else CutellyInk,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = profile.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CutellyInk
                                    )
                                }
                            }
                        }
                    }
                }

                // Diagnostics Center
                item {
                    Text(
                        text = "Permissions & Diagnostics",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = CutellyInk
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Overlay Permission status
                            DiagnosticItem(
                                title = "Display Over Other Apps",
                                description = "Allows stickers to float on phone",
                                isGranted = hasOverlayPerm,
                                onAction = {
                                    val intent = Intent(
                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:${context.packageName}")
                                    ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                                    context.startActivity(intent)
                                }
                            )

                            // Accessibility status
                            DiagnosticItem(
                                title = "Accessibility Service",
                                description = "Allows screenshot, recents, back actions",
                                isGranted = hasAccessibility,
                                onAction = {
                                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                }
                            )

                            // Foreground Overlay Service
                            DiagnosticItem(
                                title = "Overlay Engine State",
                                description = if (isOverlayActive) "Running smoothly" else "Paused",
                                isGranted = isOverlayActive,
                                onAction = {
                                    viewModel.toggleOverlayService {}
                                }
                            )
                        }
                    }
                }

                // Data Backup & Restore
                item {
                    Text(
                        text = "Backup & Restore",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = CutellyInk
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    backupJsonText = viewModel.exportConfigurationJson()
                                    showBackupDialog = true
                                },
                                modifier = Modifier.weight(1f).height(46.dp),
                                shape = RoundedCornerShape(23.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CutellyLavender)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Upload, contentDescription = null, tint = CutellyInk, modifier = Modifier.size(18.dp))
                                    Text("Export", color = CutellyInk, fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = {
                                    importInputText = ""
                                    showImportDialog = true
                                },
                                modifier = Modifier.weight(1f).height(46.dp),
                                shape = RoundedCornerShape(23.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CutellyMint)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, tint = CutellyInk, modifier = Modifier.size(18.dp))
                                    Text("Import", color = CutellyInk, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // About CUTELLY
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = CutellySoftPink.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "CUTELLY v1.0",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CutellyInk
                            )
                            Text(
                                text = "100% Offline • Free & Private • Zero Ads",
                                fontSize = 12.sp,
                                color = CutellySecondaryText
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Made with love by Rahul Shah ❤️",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CutellyInk
                            )
                        }
                    }
                }
            }
        }
    }

    // Export Dialog
    if (showBackupDialog) {
        Dialog(onDismissRequest = { showBackupDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Configuration Export", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = CutellyInk)
                    Text("Copy your configuration to save or transfer to another device:", fontSize = 12.sp, color = CutellySecondaryText)
                    OutlinedTextField(
                        value = backupJsonText,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        shape = RoundedCornerShape(14.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("CUTELLY Config", backupJsonText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied to clipboard! ✨", Toast.LENGTH_SHORT).show()
                                showBackupDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CutellyBlush)
                        ) {
                            Text("Copy Config")
                        }
                        OutlinedButton(
                            onClick = { showBackupDialog = false },
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Close", color = CutellyInk)
                        }
                    }
                }
            }
        }
    }

    // Import Dialog
    if (showImportDialog) {
        Dialog(onDismissRequest = { showImportDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Import Configuration", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = CutellyInk)
                    Text("Paste your exported configuration JSON below:", fontSize = 12.sp, color = CutellySecondaryText)
                    OutlinedTextField(
                        value = importInputText,
                        onValueChange = { importInputText = it },
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        placeholder = { Text("Paste JSON here...") },
                        shape = RoundedCornerShape(14.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val success = viewModel.importConfigurationJson(importInputText)
                                if (success) {
                                    Toast.makeText(context, "Restored successfully! ✨", Toast.LENGTH_SHORT).show()
                                    showImportDialog = false
                                } else {
                                    Toast.makeText(context, "Invalid configuration JSON", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CutellyBlush)
                        ) {
                            Text("Restore")
                        }
                        OutlinedButton(
                            onClick = { showImportDialog = false },
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Cancel", color = CutellyInk)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiagnosticItem(
    title: String,
    description: String,
    isGranted: Boolean,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CutellyInk)
            Text(text = description, fontSize = 12.sp, color = CutellySecondaryText)
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isGranted) CutellyMint else CutellySoftPink)
                .clickable { onAction() }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (isGranted) "Granted ✓" else "Enable >",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isGranted) Color(0xFF2E7D32) else CutellyBlush
            )
        }
    }
}

private fun getProfileIcon(name: String): ImageVector {
    return when (name) {
        "Favorite" -> Icons.Default.Favorite
        "School" -> Icons.Default.School
        "SportsEsports" -> Icons.Default.SportsEsports
        "Work" -> Icons.Default.Work
        "Bedtime" -> Icons.Default.Bedtime
        else -> Icons.Default.Layers
    }
}
