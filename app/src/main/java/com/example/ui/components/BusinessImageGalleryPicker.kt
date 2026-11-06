package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*

@Composable
fun BusinessImageGalleryPicker(
    photoList: List<String>,
    coverPhotoUrl: String,
    onPhotoListChange: (List<String>) -> Unit,
    onCoverPhotoChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showUrlInput by remember { mutableStateOf(false) }
    var inputCustomUrl by remember { mutableStateOf("") }

    // Modern Android Photo Picker (Zero-permission contract)
    val multiplePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 8)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val newUrls = uris.map { it.toString() }
            val updated = (photoList + newUrls).distinct()
            onPhotoListChange(updated)
            if (coverPhotoUrl.isBlank()) {
                onCoverPhotoChange(newUrls.first())
            }
        }
    }

    // Modern Android File/Document Picker
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val uriStr = uri.toString()
            if (!photoList.contains(uriStr)) {
                val updated = photoList + uriStr
                onPhotoListChange(updated)
                if (coverPhotoUrl.isBlank()) {
                    onCoverPhotoChange(uriStr)
                }
            }
        }
    }

    // Quick presets for high-end commerce demonstration
    val luxuryPresets = listOf(
        Pair("Fachada", "https://images.unsplash.com/photo-1503951914875-452162b0f3f1"),
        Pair("Interior", "https://images.unsplash.com/photo-1521590832167-7bcbfaa6381f"),
        Pair("Catálogo", "https://images.unsplash.com/photo-1555396273-367ea4eb4db5"),
        Pair("Productos", "https://images.unsplash.com/photo-1509440159596-0249088772ff")
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceElevatedDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldContainer),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Collections,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Fotografías y Galería del Negocio",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldContainer
                ) {
                    Text(
                        text = "${photoList.size} foto(s)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Inserta imágenes de alta calidad desde tu galería de fotos, explorador de archivos o enlaces web.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondaryDark,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Galería, Archivo, URL
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Button 1: Galería de Fotos
                Button(
                    onClick = {
                        multiplePhotoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = ObsidianDark
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pick_from_gallery_button")
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Galería", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                // Button 2: Archivo del Sistema
                FilledTonalButton(
                    onClick = { fileLauncher.launch("image/*") },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = SlateNavyDark,
                        contentColor = TextPrimaryDark
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pick_from_file_button")
                ) {
                    Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Archivo", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                }

                // Button 3: Enlace Web / URL
                FilledTonalButton(
                    onClick = { showUrlInput = !showUrlInput },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = SlateNavyDark,
                        contentColor = if (showUrlInput) GoldPrimary else TextSecondaryDark
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("toggle_url_input_button")
                ) {
                    Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("URL", fontSize = 11.sp)
                }
            }

            // Expandable URL Input Field
            AnimatedVisibility(visible = showUrlInput) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputCustomUrl,
                            onValueChange = { inputCustomUrl = it },
                            placeholder = { Text("https://servidor.com/foto.jpg", fontSize = 11.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark,
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = SurfaceElevatedDark
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (inputCustomUrl.isNotBlank()) {
                                    val trimmed = inputCustomUrl.trim()
                                    if (!photoList.contains(trimmed)) {
                                        val updated = photoList + trimmed
                                        onPhotoListChange(updated)
                                        if (coverPhotoUrl.isBlank()) onCoverPhotoChange(trimmed)
                                    }
                                    inputCustomUrl = ""
                                    showUrlInput = false
                                }
                            },
                            modifier = Modifier
                                .background(GoldPrimary, CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(Icons.Default.Add, null, tint = ObsidianDark, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Quick High-End Preset Chips
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Añadir demo:",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 10.sp)
                )
                luxuryPresets.forEach { (label, url) ->
                    val isAdded = photoList.contains(url)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isAdded) GoldContainer else SlateNavyDark,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                if (!isAdded) {
                                    val updated = photoList + url
                                    onPhotoListChange(updated)
                                    if (coverPhotoUrl.isBlank()) onCoverPhotoChange(url)
                                }
                            }
                    ) {
                        Text(
                            text = if (isAdded) "✓ $label" else "+ $label",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isAdded) GoldLight else TextSecondaryDark,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Selected Images Thumbnail Carousel
            if (photoList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(photoList, key = { it }) { photoUri ->
                        val isCover = photoUri == coverPhotoUrl

                        Box(
                            modifier = Modifier
                                .size(width = 110.dp, height = 80.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    width = if (isCover) 2.dp else 1.dp,
                                    color = if (isCover) GoldPrimary else SurfaceElevatedDark,
                                    shape = RoundedCornerShape(10.dp)
                                )
                        ) {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = "Foto del comercio",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Semi-transparent overlay at top for controls
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isCover) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = GoldPrimary
                                    ) {
                                        Text(
                                            text = "PORTADA",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = ObsidianDark,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Fijar portada",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.clickable { onCoverPhotoChange(photoUri) }
                                    )
                                }

                                // Delete photo button
                                IconButton(
                                    onClick = {
                                        val updated = photoList.filter { it != photoUri }
                                        onPhotoListChange(updated)
                                        if (isCover) {
                                            onCoverPhotoChange(updated.firstOrNull() ?: "")
                                        }
                                    },
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Eliminar",
                                        tint = RubyAlert,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
