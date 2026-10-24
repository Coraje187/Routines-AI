package com.familyroutines.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.familyroutines.data.local.entities.ChildEntity
import com.familyroutines.data.weather.WeatherInfo
import com.familyroutines.ui.theme.*

@Composable
fun KidProfileBar(
    children: List<ChildEntity>,
    selectedChildId: Long?,
    weather: WeatherInfo,
    isDarkTheme: Boolean,
    onSelectChild: (Long) -> Unit,
    onAddKid: () -> Unit,
    onDeleteChild: (ChildEntity) -> Unit = {},
    onEditKidPhoto: (ChildEntity) -> Unit = {},
    onWeatherClick: () -> Unit = {},
    onToggleDarkTheme: () -> Unit,
    isCompact: Boolean = false,
    modifier: Modifier = Modifier
) {
    var childToDelete by remember { mutableStateOf<ChildEntity?>(null) }

    if (isCompact) {
        // --- COMPACT PHONE HEADER: 2 neat rows ---
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 1: Genesis OS Branding on left, Weather + Theme toggle on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GenesisBrandingBadge(isCompact = true)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimeWeatherWidget(
                        weather = weather,
                        isDarkTheme = isDarkTheme,
                        isCompact = true,
                        onWeatherClick = onWeatherClick
                    )

                    IconButton(
                        onClick = onToggleDarkTheme,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0),
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Sensory Dim Mode",
                            tint = if (isDarkTheme) Color(0xFFFDE68A) else Color(0xFF475569),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Row 2: Horizontally scrollable kid chips + Add Kid button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                KidChipsContent(
                    children = children,
                    selectedChildId = selectedChildId,
                    isDarkTheme = isDarkTheme,
                    onSelectChild = onSelectChild,
                    onAddKid = onAddKid,
                    onEditKidPhoto = onEditKidPhoto,
                    onDeleteRequested = { childToDelete = it }
                )
            }
        }
    } else {
        // --- TABLET / DESKTOP WIDE HEADER: Single wide Row ---
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GenesisBrandingBadge(isCompact = false)

                KidChipsContent(
                    children = children,
                    selectedChildId = selectedChildId,
                    isDarkTheme = isDarkTheme,
                    onSelectChild = onSelectChild,
                    onAddKid = onAddKid,
                    onEditKidPhoto = onEditKidPhoto,
                    onDeleteRequested = { childToDelete = it }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TimeWeatherWidget(
                    weather = weather,
                    isDarkTheme = isDarkTheme,
                    isCompact = false,
                    onWeatherClick = onWeatherClick
                )

                IconButton(
                    onClick = onToggleDarkTheme,
                    modifier = Modifier
                        .background(
                            if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Sensory Dim Mode",
                        tint = if (isDarkTheme) Color(0xFFFDE68A) else Color(0xFF475569)
                    )
                }
            }
        }
    }

    // Confirmation Dialog for Removing a Child
    if (childToDelete != null) {
        val target = childToDelete!!
        AlertDialog(
            onDismissRequest = { childToDelete = null },
            title = {
                Text(
                    text = "Remove ${target.name}?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove ${target.name}? This will delete all of ${target.name}'s routines, chores, and notes from Routines AI.",
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteChild(target)
                        childToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Remove Child", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { childToDelete = null }) {
                    Text("Cancel", fontSize = 15.sp)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun GenesisBrandingBadge(isCompact: Boolean) {
    Row(
        modifier = Modifier
            .background(
                Color(0xFF090D16),
                RoundedCornerShape(if (isCompact) 16.dp else 20.dp)
            )
            .border(
                1.5.dp,
                Color(0xFF00F0FF),
                RoundedCornerShape(if (isCompact) 16.dp else 20.dp)
            )
            .padding(
                horizontal = if (isCompact) 10.dp else 12.dp,
                vertical = if (isCompact) 5.dp else 7.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (isCompact) 20.dp else 24.dp)
                .background(Color(0xFF00F0FF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "G",
                fontSize = if (isCompact) 12.sp else 14.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF090D16)
            )
        }
        Text(
            text = "Routines AI",
            fontSize = if (isCompact) 13.sp else 15.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF00F0FF)
        )
    }
}

@Composable
private fun KidChipsContent(
    children: List<ChildEntity>,
    selectedChildId: Long?,
    isDarkTheme: Boolean,
    onSelectChild: (Long) -> Unit,
    onAddKid: () -> Unit,
    onEditKidPhoto: (ChildEntity) -> Unit,
    onDeleteRequested: (ChildEntity) -> Unit
) {
    children.forEach { child ->
        val isSelected = child.id == selectedChildId
        val childColor = try {
            Color(android.graphics.Color.parseColor(child.colorHex))
        } catch (e: Exception) {
            PrimaryBlue
        }

        Row(
            modifier = Modifier
                .background(
                    if (isSelected) childColor else if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                    RoundedCornerShape(20.dp)
                )
                .border(
                    2.dp,
                    if (isSelected) childColor else if (isDarkTheme) Color(0xFF334155) else Color(0xFFCBD5E1),
                    RoundedCornerShape(20.dp)
                )
                .clickable { onSelectChild(child.id) }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Avatar dot / Photo / Initials
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White.copy(alpha = 0.25f) else childColor, CircleShape)
                    .clickable { onEditKidPhoto(child) },
                contentAlignment = Alignment.Center
            ) {
                if (!child.avatarUri.isNullOrBlank()) {
                    if (child.avatarUri.startsWith("preset:")) {
                        Text(
                            text = child.avatarUri.removePrefix("preset:"),
                            fontSize = 16.sp
                        )
                    } else {
                        AsyncImage(
                            model = child.avatarUri,
                            contentDescription = "${child.name} Photo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Text(
                        text = child.name.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // Kid Name
            Text(
                text = child.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else if (isDarkTheme) DarkTextPrimary else TextDark
            )

            // Star Points Pill
            Row(
                modifier = Modifier
                    .background(
                        if (isSelected) Color.Black.copy(alpha = 0.2f) else if (isDarkTheme) Color(0xFF0F172A) else Color.White,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Stars",
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${child.stars} pts",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) Color.White else Color(0xFFD97706)
                )
            }

            // Edit Kid Photo Button (shown on selected profile)
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                        .clickable { onEditKidPhoto(child) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change photo for ${child.name}",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // Remove Child Button (shown on selected profile)
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                        .clickable { onDeleteRequested(child) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove ${child.name}",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }

    // + Add Kid Pill
    Row(
        modifier = Modifier
            .background(
                if (isDarkTheme) Color(0xFF1E293B) else Color.White,
                RoundedCornerShape(20.dp)
            )
            .border(
                1.dp,
                if (isDarkTheme) Color(0xFF475569) else Color(0xFFCBD5E1),
                RoundedCornerShape(20.dp)
            )
            .clickable { onAddKid() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Kid",
            tint = if (isDarkTheme) DarkTextSecondary else TextMuted,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "Add Kid",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDarkTheme) DarkTextSecondary else TextMuted
        )
    }
}
