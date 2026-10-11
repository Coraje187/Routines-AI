package com.familyroutines.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.familyroutines.data.local.entities.FamilyPhotoEntity
import com.familyroutines.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun FamilyPhotoFrameScreen(
    photos: List<FamilyPhotoEntity>,
    isDarkTheme: Boolean,
    onAddPhoto: () -> Unit,
    onDeletePhoto: (FamilyPhotoEntity) -> Unit,
    onUpdateCaption: (FamilyPhotoEntity, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isSlideshowActive by remember { mutableStateOf(false) }
    var showCaptionDialog by remember { mutableStateOf(false) }

    // Auto-advance slideshow if enabled
    LaunchedEffect(isSlideshowActive, photos.size, currentIndex) {
        if (isSlideshowActive && photos.size > 1) {
            delay(10000)
            currentIndex = (currentIndex + 1) % photos.size
        }
    }

    // Keep index bounded
    val activeIndex = if (photos.isEmpty()) 0 else currentIndex.coerceIn(0, photos.size - 1)
    val activePhoto = photos.getOrNull(activeIndex)

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🖼️ Family Photo Frame",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) DarkTextPrimary else TextDark
                )
                if (photos.isNotEmpty()) {
                    Text(
                        text = "(${activeIndex + 1} of ${photos.size})",
                        fontSize = 14.sp,
                        color = if (isDarkTheme) DarkTextSecondary else TextMuted
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (photos.size > 1) {
                    FilterChip(
                        selected = isSlideshowActive,
                        onClick = { isSlideshowActive = !isSlideshowActive },
                        label = {
                            Text(if (isSlideshowActive) "⏸ Pause Slideshow" else "▶ Slideshow")
                        }
                    )
                }

                Button(
                    onClick = onAddPhoto,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Add Family Photo",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Photo")
                }
            }
        }

        if (photos.isEmpty()) {
            // Empty State Frame
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Camera",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Your Family Memories Live Here",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) DarkTextPrimary else TextDark
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Add photos of the kids, pets, and family trips to display as an ambient wall frame.",
                        fontSize = 15.sp,
                        color = if (isDarkTheme) DarkTextSecondary else TextMuted
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onAddPhoto,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Upload", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Choose Photo from Tablet", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Active Photo Display Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) Color(0xFF020617) else Color(0xFF0F172A)
                )
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Image with smooth Crossfade transition
                    Crossfade(
                        targetState = activePhoto,
                        label = "PhotoCrossfade",
                        modifier = Modifier.fillMaxSize()
                    ) { photo ->
                        if (photo != null) {
                            AsyncImage(
                                model = photo.photoUri,
                                contentDescription = photo.caption.ifBlank { "Family Photo" },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(20.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    // Navigation Chevrons
                    if (photos.size > 1) {
                        IconButton(
                            onClick = {
                                currentIndex = if (currentIndex > 0) currentIndex - 1 else photos.size - 1
                            },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 12.dp)
                                .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Photo", tint = Color.White)
                        }

                        IconButton(
                            onClick = {
                                currentIndex = (currentIndex + 1) % photos.size
                            },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 12.dp)
                                .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next Photo", tint = Color.White)
                        }
                    }

                    // Bottom Caption Gradient Bar
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                )
                            )
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = activePhoto?.caption?.ifBlank { "Family Memory ❤️" } ?: "Family Memory ❤️",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { showCaptionDialog = true },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Caption",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    activePhoto?.let { onDeletePhoto(it) }
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete Photo",
                                    tint = Color(0xFFF87171),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Thumbnail Strip at the Bottom
            if (photos.size > 1) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(photos) { index, photo ->
                        val isSelected = index == activeIndex
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) PrimaryBlue else Color.Gray.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { currentIndex = index }
                        ) {
                            AsyncImage(
                                model = photo.photoUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }

    // Caption Editing Dialog
    if (showCaptionDialog && activePhoto != null) {
        var newCaption by remember { mutableStateOf(activePhoto.caption) }
        AlertDialog(
            onDismissRequest = { showCaptionDialog = false },
            title = { Text("Edit Photo Caption", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newCaption,
                    onValueChange = { newCaption = it },
                    label = { Text("Caption") },
                    placeholder = { Text("e.g. Vacation 2026, Leo's Birthday...") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateCaption(activePhoto, newCaption)
                        showCaptionDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCaptionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
