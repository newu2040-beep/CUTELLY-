package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.provider.Settings
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActionType
import com.example.data.model.GestureMapping
import com.example.data.model.GestureType
import com.example.service.ActionExecutor
import com.example.service.CutellyAccessibilityService
import com.example.ui.components.CutellyTopBar
import com.example.ui.theme.CutellyBlush
import com.example.ui.theme.CutellyCardBorder
import com.example.ui.theme.CutellyInk
import com.example.ui.theme.CutellyMint
import com.example.ui.theme.CutellyPeach
import com.example.ui.theme.CutellySecondaryText
import com.example.ui.theme.CutellySoftPink
import com.example.ui.viewmodel.CutellyViewModel
import com.example.util.KawaiiHaptics
import com.example.util.KawaiiSoundManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesturesScreen(
    viewModel: CutellyViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val gestureMappings by viewModel.gestureMappings.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Custom", "System", "Apps", "Navigation", "Media", "Cutelly")

    var editingMapping by remember { mutableStateOf<GestureMapping?>(null) }
    var isCreatingNewGesture by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    val filteredMappings = remember(gestureMappings, selectedFilter) {
        gestureMappings.filter { mapping ->
            when (selectedFilter) {
                "All" -> true
                "Custom" -> mapping.isCustom
                "System" -> mapping.actionType.category == "System"
                "Apps" -> mapping.actionType.category == "Apps"
                "Navigation" -> mapping.actionType.category == "Navigation"
                "Media" -> mapping.actionType.category == "Media"
                "Cutelly" -> mapping.actionType.category == "Cutelly"
                else -> true
            }
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
                CutellyTopBar(
                    title = "Gestures & Shortcuts",
                    showBack = true,
                    onBack = onBack,
                    showHelp = true,
                    onHelp = { showHelpDialog = true }
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Top Header Card with Kitten Mascot
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(26.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TouchApp,
                                        contentDescription = "Gestures",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Smart Kawaii Actions ✨",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Tap, drag, double-tap, or shake your phone to trigger screenshots, apps & sleep mode!",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Create Custom Gesture Button Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isCreatingNewGesture = true
                                    KawaiiSoundManager.playSound("bubble")
                                },
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
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
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Create Custom Gesture",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Choose any trigger & assign any action",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Open",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Filter Chips
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filters) { filter ->
                                val isSelected = filter == selectedFilter
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                        .border(
                                            1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            selectedFilter = filter
                                            KawaiiSoundManager.playSound("soft_click")
                                        }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = filter,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Gesture List Items
                    items(filteredMappings, key = { it.id }) { mapping ->
                        GestureCardItem(
                            mapping = mapping,
                            onToggle = { viewModel.toggleGestureEnabled(mapping.id) },
                            onEdit = { editingMapping = mapping },
                            onDelete = if (mapping.isCustom) {
                                { viewModel.deleteGestureMapping(mapping.id) }
                            } else null,
                            onTest = {
                                ActionExecutor.execute(context, mapping)
                            }
                        )
                    }

                    if (filteredMappings.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("No gestures in this category", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Edit Existing Gesture Sheet
        editingMapping?.let { mapping ->
            GestureEditSheet(
                mapping = mapping,
                onDismiss = { editingMapping = null },
                onSave = { updated ->
                    viewModel.updateGestureMapping(updated)
                    editingMapping = null
                }
            )
        }

        // Create New Custom Gesture Sheet
        if (isCreatingNewGesture) {
            CustomGestureCreatorSheet(
                onDismiss = { isCreatingNewGesture = false },
                onSave = { name, trigger, action, target, label, sound, anim ->
                    viewModel.createCustomGesture(name, trigger, action, target, label, sound, anim)
                    isCreatingNewGesture = false
                }
            )
        }

        // Help Dialog
        if (showHelpDialog) {
            GestureHelpDialog(onDismiss = { showHelpDialog = false })
        }
    }
}

@Composable
fun GestureCardItem(
    mapping: GestureMapping,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: (() -> Unit)?,
    onTest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(getActionColor(mapping.actionType).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getActionIcon(mapping.actionType),
                            contentDescription = mapping.actionLabel,
                            tint = getActionColor(mapping.actionType),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = mapping.gestureName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (mapping.isCustom) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CutellyBlush.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Custom", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CutellyBlush)
                                }
                            }
                        }
                        Text(
                            text = mapping.gestureType.displayName + " • " + mapping.actionLabel,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = mapping.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            // Quick actions on gesture
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Test button
                OutlinedButton(
                    onClick = onTest,
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(17.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Test", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }

                if (onDelete != null) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomGestureCreatorSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, trigger: GestureType, action: ActionType, target: String, label: String, sound: String, anim: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    var gestureName by remember { mutableStateOf("") }
    var selectedTrigger by remember { mutableStateOf(GestureType.DOUBLE_TAP) }
    var selectedActionType by remember { mutableStateOf(ActionType.TAKE_SCREENSHOT) }
    var selectedActionLabel by remember { mutableStateOf("Take Screenshot") }
    var selectedTarget by remember { mutableStateOf("") }
    var selectedSound by remember { mutableStateOf("pop") }
    var selectedAnim by remember { mutableStateOf("SOFT_BOUNCE") }

    val triggers = GestureType.values().toList()
    val actions = ActionType.values().toList()
    val sounds = listOf("pop", "bubble", "chime", "sparkle", "soft_click", "none")

    // Installed apps
    val installedApps = remember {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        pm.queryIntentActivities(intent, 0)
            .map { it.activityInfo.packageName to it.loadLabel(pm).toString() }
            .distinctBy { it.first }
            .sortedBy { it.second }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "✨ Create Custom Gesture",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // 1. Gesture Name
            OutlinedTextField(
                value = gestureName,
                onValueChange = { gestureName = it },
                label = { Text("Gesture Name (e.g. Secret Cat Tap)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            // 2. Gesture Trigger
            Text("Select Trigger Gesture", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(triggers) { trig ->
                    val isSelected = trig == selectedTrigger
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedTrigger = trig }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = trig.displayName,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // 3. Action Assignment
            Text("Select Action to Trigger", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(actions) { act ->
                    val isSelected = act == selectedActionType
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                selectedActionType = act
                                selectedActionLabel = act.displayName
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(
                                imageVector = getActionIcon(act),
                                contentDescription = act.displayName,
                                modifier = Modifier.size(14.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = act.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // App Picker if Launch App
            if (selectedActionType == ActionType.OPEN_APP) {
                Text("Select Application to Launch", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(installedApps.take(20)) { (pkg, label) ->
                        val isSelected = selectedTarget == pkg
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) CutellyMint else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    selectedTarget = pkg
                                    selectedActionLabel = "Open $label"
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            // 4. Sound Effect
            Text("Sound Effect", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sounds) { s ->
                    val isSelected = s == selectedSound
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) CutellySoftPink else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                selectedSound = s
                                if (s != "none") KawaiiSoundManager.playSound(s)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = s.replaceFirstChar { it.uppercase() }, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            // Save & Test
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val testMapping = GestureMapping(
                            id = "test",
                            gestureName = "Test",
                            gestureType = selectedTrigger,
                            actionType = selectedActionType,
                            actionTarget = selectedTarget,
                            actionLabel = selectedActionLabel,
                            soundEffect = selectedSound
                        )
                        ActionExecutor.execute(context, testMapping)
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Test Action", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        onSave(
                            gestureName.ifEmpty { selectedTrigger.displayName },
                            selectedTrigger,
                            selectedActionType,
                            selectedTarget,
                            selectedActionLabel,
                            selectedSound,
                            selectedAnim
                        )
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save Gesture ✨", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestureEditSheet(
    mapping: GestureMapping,
    onDismiss: () -> Unit,
    onSave: (GestureMapping) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    var selectedActionType by remember { mutableStateOf(mapping.actionType) }
    var selectedActionLabel by remember { mutableStateOf(mapping.actionLabel) }
    var selectedTarget by remember { mutableStateOf(mapping.actionTarget) }
    var selectedSound by remember { mutableStateOf(mapping.soundEffect) }

    val actions = ActionType.values().toList()
    val sounds = listOf("pop", "bubble", "chime", "sparkle", "soft_click", "none")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Edit Gesture: ${mapping.gestureName}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text("Assigned Action", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(actions) { act ->
                    val isSelected = act == selectedActionType
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                selectedActionType = act
                                selectedActionLabel = act.displayName
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = act.displayName,
                            fontSize = 12.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Text("Sound Effect", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sounds) { s ->
                    val isSelected = s == selectedSound
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) CutellySoftPink else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                selectedSound = s
                                if (s != "none") KawaiiSoundManager.playSound(s)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = s.replaceFirstChar { it.uppercase() }, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val test = mapping.copy(
                            actionType = selectedActionType,
                            actionLabel = selectedActionLabel,
                            actionTarget = selectedTarget,
                            soundEffect = selectedSound
                        )
                        ActionExecutor.execute(context, test)
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Test Action", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        onSave(
                            mapping.copy(
                                actionType = selectedActionType,
                                actionLabel = selectedActionLabel,
                                actionTarget = selectedTarget,
                                soundEffect = selectedSound
                            )
                        )
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun GestureHelpDialog(onDismiss: () -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("How Gestures Work ✨", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("• Single Tap: Quick shortcut on your floating sticker.")
                Text("• Double Tap: Fast double click to open camera or apps.")
                Text("• Long Press: Hold for 0.5s to open the mini menu or trigger custom shortcuts.")
                Text("• Shakes: Shake phone 3, 4, or 5 times from anywhere to take screenshots or toggle flashlight.")
                Text("• Sleep Mode: Put sticker to sleep or drag it to the bottom target to dismiss.")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = CutellyBlush)) {
                Text("Got it! 💖")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

fun getActionIcon(actionType: ActionType): ImageVector = when (actionType) {
    ActionType.TAKE_SCREENSHOT -> Icons.Default.Screenshot
    ActionType.OPEN_CAMERA -> Icons.Default.CameraAlt
    ActionType.OPEN_GALLERY -> Icons.Default.Layers
    ActionType.OPEN_BROWSER -> Icons.Default.Wifi
    ActionType.OPEN_DIALER -> Icons.Default.Smartphone
    ActionType.OPEN_MESSAGING -> Icons.Default.TouchApp
    ActionType.TOGGLE_FLASHLIGHT -> Icons.Default.FlashlightOn
    ActionType.OPEN_QUICK_SETTINGS -> Icons.Default.PowerSettingsNew
    ActionType.OPEN_NOTIFICATIONS -> Icons.Default.Notifications
    ActionType.OPEN_RECENTS -> Icons.Default.Apps
    ActionType.NAVIGATE_HOME -> Icons.Default.TouchApp
    ActionType.NAVIGATE_BACK -> Icons.Default.TouchApp
    ActionType.LOCK_SCREEN -> Icons.Default.Lock
    ActionType.POWER_DIALOG -> Icons.Default.PowerSettingsNew
    ActionType.OPEN_APP -> Icons.Default.Apps
    ActionType.MEDIA_PLAY_PAUSE -> Icons.Default.PlayArrow
    ActionType.MEDIA_NEXT -> Icons.Default.PlayArrow
    ActionType.MEDIA_PREVIOUS -> Icons.Default.PlayArrow
    ActionType.VOLUME_UP -> Icons.Default.VolumeUp
    ActionType.VOLUME_DOWN -> Icons.Default.VolumeUp
    ActionType.TOGGLE_MUTE -> Icons.Default.Vibration
    ActionType.OPEN_MUSIC_APP -> Icons.Default.MusicNote
    ActionType.SLEEP_STICKER -> Icons.Default.Bedtime
    ActionType.DISMISS_STICKER -> Icons.Default.Bedtime
    ActionType.CUTELLY_SHOW_HIDE -> Icons.Default.TouchApp
    ActionType.CUTELLY_CHANGE_STICKER -> Icons.Default.Star
    ActionType.CUTELLY_OPEN_GALLERY -> Icons.Default.Layers
    ActionType.CUTELLY_PLAY_SOUND -> Icons.Default.VolumeUp
    ActionType.CUTELLY_OPEN_MENU -> Icons.Default.Apps
    ActionType.SHOW_CUTE_MESSAGE -> Icons.Default.Star
    else -> Icons.Default.TouchApp
}

fun getActionColor(actionType: ActionType): Color = when (actionType.category) {
    "System" -> Color(0xFFFF78AB)
    "Apps" -> Color(0xFF9D7BFF)
    "Navigation" -> Color(0xFF48A873)
    "Media" -> Color(0xFFE2B024)
    "Cutelly" -> Color(0xFFFF8557)
    else -> Color(0xFFFF78AB)
}
