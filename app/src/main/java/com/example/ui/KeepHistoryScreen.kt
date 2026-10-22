package com.example.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.backup.BackupManager
import com.example.data.db.CalculationEntity
import com.example.ui.theme.BebasNeueFontFamily
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun KeepHistoryScreen(
    calculations: List<CalculationEntity>,
    customTags: List<String>,
    accentColor: Color,
    onCallBack: (CalculationEntity) -> Unit,
    onUpdateCalculation: (CalculationEntity) -> Unit,
    onDeleteCalculation: (Long) -> Unit,
    onDeleteCalculations: (Set<Long>) -> Unit,
    onAddTag: (String) -> Unit,
    onDeleteTag: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<Long>()) }
    var showBatchDeleteConfirm by remember { mutableStateOf(false) }

    // Intercept Android hardware/gesture back button to exit selection mode or return to home
    BackHandler {
        if (isSelectionMode) {
            isSelectionMode = false
            selectedIds = emptySet()
        } else {
            onDismiss()
        }
    }

    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("All") }
    var editingCalculation by remember { mutableStateOf<CalculationEntity?>(null) }
    var showTagManager by remember { mutableStateOf(false) }

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val combinedTags = (listOf("All") + customTags).distinct()

    val filteredList = calculations.filter { calc ->
        val matchesQuery = searchQuery.isBlank() ||
                calc.sessionName.contains(searchQuery, ignoreCase = true) ||
                calc.formula.contains(searchQuery, ignoreCase = true) ||
                calc.result.contains(searchQuery, ignoreCase = true) ||
                calc.notes.contains(searchQuery, ignoreCase = true) ||
                calc.getAllEntryTags().any { it.contains(searchQuery, ignoreCase = true) }

        val matchesTag = selectedTag == "All" || calc.templateTag.equals(selectedTag, ignoreCase = true)
        matchesQuery && matchesTag
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1014))
            .testTag("keep_history_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = statusBarPadding + 6.dp,
                    bottom = navBarPadding,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            // Top Bar: Normal Mode vs. Multi-Select Mode
            if (isSelectionMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            isSelectionMode = false
                            selectedIds = emptySet()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel Selection",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "${selectedIds.size} Selected",
                        fontFamily = BebasNeueFontFamily,
                        fontSize = 24.sp,
                        color = Color.White,
                        letterSpacing = 1.sp,
                        modifier = Modifier.weight(1f)
                    )

                    // Select All / Deselect All
                    TextButton(
                        onClick = {
                            selectedIds = if (selectedIds.size == filteredList.size) {
                                emptySet()
                            } else {
                                filteredList.map { it.id }.toSet()
                            }
                        }
                    ) {
                        Text(
                            text = if (selectedIds.size == filteredList.size) "Deselect All" else "Select All",
                            color = accentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Delete Selected Button
                    IconButton(
                        onClick = {
                            if (selectedIds.isNotEmpty()) {
                                showBatchDeleteConfirm = true
                            }
                        },
                        enabled = selectedIds.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Selected",
                            tint = if (selectedIds.isNotEmpty()) Color(0xFFEF4444) else Color(0xFF6B7280)
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("history_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Calculator",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Calculation Keep",
                        fontFamily = BebasNeueFontFamily,
                        fontSize = 28.sp,
                        color = Color.White,
                        letterSpacing = 1.sp,
                        modifier = Modifier.weight(1f)
                    )

                    // Backup to Drive Button
                    IconButton(
                        onClick = {
                            val json = BackupManager.exportToJson(calculations)
                            BackupManager.shareBackupToDrive(context, json)
                        },
                        modifier = Modifier.testTag("drive_backup_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Backup to Google Drive",
                            tint = accentColor
                        )
                    }

                    // Delete / Multi-Select Mode Button beside Cloud Button
                    IconButton(
                        onClick = {
                            isSelectionMode = true
                            selectedIds = emptySet()
                        },
                        modifier = Modifier.testTag("keep_multi_select_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Select and Delete Calculations",
                            tint = Color(0xFFEF4444)
                        )
                    }
                }
            }

            // Google Keep Search Bar with Icon
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by title, tags, formula, or cost...", color = Color(0xFF7E8492), fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF9CA3AF)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0x18FFFFFF),
                    unfocusedContainerColor = Color(0x10FFFFFF),
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = Color(0x22FFFFFF),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("keep_search_input")
            )

            // Category Filter Chips + Edit Tags Button
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                items(combinedTags) { tag ->
                    val isSelected = selectedTag == tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) accentColor.copy(alpha = 0.25f) else Color(0x12FFFFFF)
                            )
                            .border(
                                1.dp,
                                if (isSelected) accentColor else Color(0x20FFFFFF),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedTag = tag }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) accentColor else Color(0xFFC7CDD8)
                        )
                    }
                }

                // Edit Tags Action Button at end of scroll row
                item {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x18FFFFFF))
                            .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .clickable { showTagManager = true }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("edit_tags_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Manage Tags",
                                tint = accentColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Edit Tags",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = accentColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Keep History Staggered Grid
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (searchQuery.isEmpty()) "No calculations saved yet" else "No matching calculations found",
                            color = Color(0xFF7E8492),
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Calculations and named sessions will appear here as Keep notes.",
                            color = Color(0xFF5A5F6B),
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp,
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .testTag("keep_staggered_grid")
                ) {
                    items(filteredList, key = { it.id }) { calc ->
                        val isCardSelected = selectedIds.contains(calc.id)
                        KeepCalculationCard(
                            calculation = calc,
                            accentColor = accentColor,
                            isSelectionMode = isSelectionMode,
                            isSelected = isCardSelected,
                            onToggleSelection = {
                                selectedIds = if (isCardSelected) {
                                    selectedIds - calc.id
                                } else {
                                    selectedIds + calc.id
                                }
                            },
                            onCallBack = { onCallBack(calc) },
                            onCopy = { BackupManager.copySnippetToClipboard(context, calc) },
                            onShare = { BackupManager.shareCalculation(context, calc) },
                            onEdit = { editingCalculation = calc },
                            onTogglePin = {
                                onUpdateCalculation(calc.copy(isPinned = !calc.isPinned))
                            },
                            onDelete = { onDeleteCalculation(calc.id) }
                        )
                    }
                }
            }
        }

        // Edit / Rename Calculation Dialog
        editingCalculation?.let { calc ->
            RenameKeepDialog(
                calculation = calc,
                accentColor = accentColor,
                onDismiss = { editingCalculation = null },
                onSave = { updated ->
                    onUpdateCalculation(updated)
                    editingCalculation = null
                }
            )
        }

        // Manage Tags Dialog (Add / Delete all tags)
        if (showTagManager) {
            TagManagementDialog(
                allTags = combinedTags,
                accentColor = accentColor,
                onAddTag = onAddTag,
                onDeleteTag = onDeleteTag,
                onDismiss = { showTagManager = false }
            )
        }

        // Batch Delete Confirmation Dialog
        if (showBatchDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showBatchDeleteConfirm = false },
                containerColor = Color(0xFF1E2029),
                shape = RoundedCornerShape(20.dp),
                title = {
                    Text(
                        text = "Delete ${selectedIds.size} Calculations?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to delete these calculations from Keep?",
                        color = Color(0xFFBAC0CD),
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteCalculations(selectedIds)
                            selectedIds = emptySet()
                            isSelectionMode = false
                            showBatchDeleteConfirm = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("Delete", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBatchDeleteConfirm = false }) {
                        Text("Cancel", color = Color(0xFF9CA3AF))
                    }
                }
            )
        }
    }
}

@Composable
private fun KeepCalculationCard(
    calculation: CalculationEntity,
    accentColor: Color,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onToggleSelection: () -> Unit,
    onCallBack: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onEdit: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(calculation.timestamp))
    val allEntryTags = calculation.getAllEntryTags()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(calculation.cardColorHex).copy(alpha = 0.22f),
                        Color(0x14FFFFFF),
                        Color(0x18000000)
                    )
                )
            )
            .border(
                1.dp,
                if (isSelectionMode && isSelected) {
                    accentColor
                } else if (calculation.isPinned) {
                    accentColor.copy(alpha = 0.8f)
                } else {
                    Color(0x24FFFFFF)
                },
                RoundedCornerShape(18.dp)
            )
            .clickable {
                if (isSelectionMode) {
                    onToggleSelection()
                }
            }
            .padding(12.dp)
            .testTag("keep_card_${calculation.id}")
    ) {
        Column {
            // Card Header: Session Name + Selection Checkbox OR Pin
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelectionMode) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = if (isSelected) "Selected" else "Unselected",
                        tint = if (isSelected) accentColor else Color(0xFF6B7280),
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 4.dp)
                    )
                }

                Text(
                    text = calculation.sessionName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (!isSelectionMode) {
                    IconButton(
                        onClick = onTogglePin,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (calculation.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin calculation",
                            tint = if (calculation.isPinned) accentColor else Color(0xFF6B7280),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Category tag badge
            if (calculation.templateTag.isNotEmpty() && calculation.templateTag != "General") {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = calculation.templateTag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Large Result Output
            Text(
                text = calculation.result,
                fontFamily = BebasNeueFontFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Formula
            Text(
                text = calculation.formula,
                fontSize = 12.sp,
                color = Color(0xFFA1A8B8),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // 1. NOTES / BREAKDOWN DETAIL MOVED ABOVE TAGS
            if (calculation.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = calculation.notes,
                    fontSize = 12.sp,
                    color = Color(0xFFD1D5DB),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 2. ALL ENTRY TAGS SCROLLABLE LAZYROW BELOW NOTES
            if (allEntryTags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(allEntryTags) { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(accentColor.copy(alpha = 0.18f))
                                .border(0.8.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = accentColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = dateStr,
                fontSize = 10.sp,
                color = Color(0xFF6B7280)
            )

            if (!isSelectionMode) {
                Spacer(modifier = Modifier.height(10.dp))

                // Action Row: Call Back, Copy, Share, Edit, Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Call back button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .clickable { onCallBack() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardReturn,
                                contentDescription = "Call back to calculator",
                                tint = accentColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Call Back", fontSize = 10.sp, color = accentColor, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = onCopy,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Snippet",
                                tint = Color(0xFFB5BAC6),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        IconButton(
                            onClick = onShare,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color(0xFFB5BAC6),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit note and tags",
                                tint = Color(0xFFB5BAC6),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RenameKeepDialog(
    calculation: CalculationEntity,
    accentColor: Color,
    onDismiss: () -> Unit,
    onSave: (CalculationEntity) -> Unit
) {
    var title by remember { mutableStateOf(calculation.sessionName) }
    var notes by remember { mutableStateOf(calculation.notes) }
    var tag by remember { mutableStateOf(calculation.templateTag) }
    var allTagsString by remember {
        mutableStateOf(calculation.getAllEntryTags().joinToString(", "))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF191B22),
        title = {
            Text(
                "Edit Calculation",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title / Session Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // 1. NOTES / BREAKDOWN DETAIL MOVED ABOVE TAGS
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Breakdown Details") },
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // 2. CATEGORY TAG WITHOUT "e.g."
                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("Category Tag") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // 3. ALL ENTRY TAGS (comma-separated, no "e.g.")
                OutlinedTextField(
                    value = allTagsString,
                    onValueChange = { allTagsString = it },
                    label = { Text("Entry Tags (comma separated)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedTags = allTagsString.split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }

                    val tagsJsonArray = JSONArray()
                    parsedTags.forEach { tagsJsonArray.put(it) }

                    val op1 = parsedTags.getOrNull(0) ?: ""
                    val op2 = parsedTags.getOrNull(1) ?: ""

                    onSave(
                        calculation.copy(
                            sessionName = title.ifBlank { "Calculation" },
                            templateTag = tag.ifBlank { "General" },
                            notes = notes,
                            operand1Tag = op1,
                            operand2Tag = op2,
                            entryTagsJson = tagsJsonArray.toString()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF9CA3AF))
            }
        }
    )
}
