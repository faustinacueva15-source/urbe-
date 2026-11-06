package com.example.ui.business

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.components.BusinessImageGalleryPicker
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun EditBusinessDialog(
    business: Business,
    onDismiss: () -> Unit,
    onSaveUpdates: (Business) -> Unit
) {
    var name by remember { mutableStateOf(business.name) }
    var description by remember { mutableStateOf(business.description) }
    var address by remember { mutableStateOf(business.address) }
    var phone by remember { mutableStateOf(business.phone) }
    var openingHours by remember { mutableStateOf(business.openingHours) }
    var websiteUrl by remember { mutableStateOf(business.websiteUrl ?: "") }
    var coverPhotoUrl by remember { mutableStateOf(business.coverPhotoUrl ?: "") }
    var photoList by remember { mutableStateOf(if (business.photoUrls.isNotEmpty()) business.photoUrls else if (!business.coverPhotoUrl.isNullOrBlank()) listOf(business.coverPhotoUrl) else emptyList()) }
    var isOpen by remember { mutableStateOf(business.isOpen) }

    // New item to add
    var newItemName by remember { mutableStateOf("") }
    var newItemPrice by remember { mutableStateOf("") }
    var newItemCategory by remember { mutableStateOf("Catálogo") }

    // New promo to add
    var newPromoTitle by remember { mutableStateOf("") }
    var newPromoCode by remember { mutableStateOf("") }
    var newPromoDiscount by remember { mutableStateOf("15") }

    var currentMenuItems by remember { mutableStateOf(business.menuItems) }
    var currentPromos by remember { mutableStateOf(business.promotions) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SlateNavyDark,
            tonalElevation = 10.dp,
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("edit_business_dialog")
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Editar Negocio & Catálogo",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                            )
                            Text(
                                text = "Actualiza fotos, precios, link web y promos",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, null, tint = TextSecondaryDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre Comercial") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = websiteUrl,
                        onValueChange = { websiteUrl = it },
                        label = { Text("Página Web / Red Social") },
                        leadingIcon = { Icon(Icons.Default.Language, null, tint = GoldPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    BusinessImageGalleryPicker(
                        photoList = photoList,
                        coverPhotoUrl = coverPhotoUrl,
                        onPhotoListChange = { photoList = it },
                        onCoverPhotoChange = { coverPhotoUrl = it }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isOpen) "Estado: Abierto Ahora" else "Estado: Local Cerrado",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isOpen) EmeraldTrust else RubyAlert,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Switch(
                            checked = isOpen,
                            onCheckedChange = { isOpen = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ObsidianDark, checkedTrackColor = EmeraldTrust)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Products list
                    Text(
                        text = "Productos en Menú / Precios Vigentes:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = GoldLight)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                items(currentMenuItems, key = { it.id }) { item ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceCardDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = TextPrimaryDark))
                                Text("S/ ${String.format("%.2f", item.price)}", style = MaterialTheme.typography.labelSmall.copy(color = GoldLight))
                            }
                            IconButton(
                                onClick = { currentMenuItems = currentMenuItems.filter { it.id != item.id } },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Delete, null, tint = RubyAlert, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                item {
                    // Add extra product field
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newItemName,
                            onValueChange = { newItemName = it },
                            placeholder = { Text("Nuevo ítem", fontSize = 11.sp) },
                            modifier = Modifier.weight(1.4f),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                        )
                        OutlinedTextField(
                            value = newItemPrice,
                            onValueChange = { newItemPrice = it },
                            placeholder = { Text("Precio S/", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                        )
                        IconButton(
                            onClick = {
                                val priceVal = newItemPrice.toDoubleOrNull()
                                if (newItemName.isNotBlank() && priceVal != null) {
                                    currentMenuItems = currentMenuItems + MenuItem(
                                        id = "m_${UUID.randomUUID().toString().take(6)}",
                                        businessId = business.id,
                                        name = newItemName,
                                        description = "Especialidad añadida",
                                        price = priceVal,
                                        category = newItemCategory,
                                        isAvailable = true
                                    )
                                    newItemName = ""
                                    newItemPrice = ""
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.AddCircle, null, tint = GoldPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Promotions section
                    Text(
                        text = "Promociones Vigentes:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = GoldLight)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                items(currentPromos, key = { it.id }) { promo ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = GoldContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(promo.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = OnGoldContainer))
                                Text("Código: ${promo.promoCode} • ${promo.discountPercent}% OFF", style = MaterialTheme.typography.labelSmall.copy(color = GoldLight))
                            }
                            IconButton(
                                onClick = { currentPromos = currentPromos.filter { it.id != promo.id } },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Delete, null, tint = RubyAlert, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                item {
                    // Add extra promo field
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newPromoTitle,
                            onValueChange = { newPromoTitle = it },
                            placeholder = { Text("Nueva promo", fontSize = 11.sp) },
                            modifier = Modifier.weight(1.4f),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                        )
                        OutlinedTextField(
                            value = newPromoCode,
                            onValueChange = { newPromoCode = it },
                            placeholder = { Text("Código", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                        )
                        IconButton(
                            onClick = {
                                if (newPromoTitle.isNotBlank()) {
                                    currentPromos = currentPromos + Promotion(
                                        id = "p_${UUID.randomUUID().toString().take(6)}",
                                        businessId = business.id,
                                        title = newPromoTitle,
                                        description = "Oferta activa actualizada",
                                        discountPercent = newPromoDiscount.toIntOrNull() ?: 15,
                                        promoCode = if (newPromoCode.isNotBlank()) newPromoCode else "OFERTA",
                                        expiresText = "Vigente",
                                        isExclusive = true
                                    )
                                    newPromoTitle = ""
                                    newPromoCode = ""
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.AddCircle, null, tint = GoldPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar", color = TextSecondaryDark)
                        }

                        Button(
                            onClick = {
                                val updated = business.copy(
                                    name = name.trim(),
                                    description = description,
                                    address = address,
                                    phone = phone,
                                    openingHours = openingHours,
                                    websiteUrl = websiteUrl.ifBlank { null },
                                    coverPhotoUrl = if (coverPhotoUrl.isNotBlank()) coverPhotoUrl else photoList.firstOrNull(),
                                    photoUrls = photoList,
                                    isOpen = isOpen,
                                    menuItems = currentMenuItems,
                                    promotions = currentPromos
                                )
                                onSaveUpdates(updated)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianDark),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Guardar Cambios", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
