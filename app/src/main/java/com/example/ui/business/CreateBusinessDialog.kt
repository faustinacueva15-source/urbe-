package com.example.ui.business

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.components.BusinessImageGalleryPicker
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun CreateBusinessDialog(
    initialLat: Double,
    initialLng: Double,
    onDismiss: () -> Unit,
    onSaveAndPublish: (Business) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(BusinessCategory.BARBERIA) }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("Av. José Larco 650, Miraflores") }
    var phone by remember { mutableStateOf("+51 987 112 334") }
    var openingHours by remember { mutableStateOf("Lun - Sáb: 09:00 - 21:00") }
    var websiteUrl by remember { mutableStateOf("https://www.mi-negocio.com") }
    var coverPhotoUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1503951914875-452162b0f3f1") }
    var photoList by remember { mutableStateOf(listOf("https://images.unsplash.com/photo-1503951914875-452162b0f3f1")) }
    var isOpenInitially by remember { mutableStateOf(true) }

    // Coordinates
    var lat by remember { mutableDoubleStateOf(initialLat) }
    var lng by remember { mutableDoubleStateOf(initialLng) }

    // Products / Menu Items (allows multiple items)
    var prod1Name by remember { mutableStateOf("Servicio Signature") }
    var prod1Price by remember { mutableStateOf("35.00") }
    var prod1Desc by remember { mutableStateOf("Elaborado con atención de primer nivel y calidad garantizada") }

    var prod2Name by remember { mutableStateOf("Combo Especial") }
    var prod2Price by remember { mutableStateOf("60.00") }
    var prod2Desc by remember { mutableStateOf("Incluye atención completa y bebida de cortesía") }

    // Active Promotion
    var hasPromo by remember { mutableStateOf(true) }
    var promoTitle by remember { mutableStateOf("20% de Descuento de Inauguración") }
    var promoDiscount by remember { mutableStateOf("20") }
    var promoCode by remember { mutableStateOf("URBE20") }
    var promoExpires by remember { mutableStateOf("Válido todo el mes") }

    // Youth Job Opening
    var hasYouthJob by remember { mutableStateOf(true) }
    var jobTitle by remember { mutableStateOf("Asistente y Atención al Cliente (Sin experiencia previa)") }
    var jobSalary by remember { mutableStateOf("S/ 900 + Capacitación") }

    // Legal documents (Explicitly OPTIONAL per user request)
    var rucNumber by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }

    var publishImmediately by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SlateNavyDark,
            tonalElevation = 10.dp,
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("create_business_dialog")
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AddBusiness, null, tint = ObsidianDark, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Crear Nuevo Negocio (Admin)",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GoldLight
                                    )
                                )
                                Text(
                                    text = "Especificaciones completas y catálogo para el mapa",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, null, tint = TextSecondaryDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Business Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre Comercial del Negocio *") },
                        placeholder = { Text("Ej. Barbería Real / Dulce Atelier / Bodega Central") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_business_name"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = SurfaceElevatedDark,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Category Selector
                    Text(
                        text = "Categoría del Comercio:",
                        style = MaterialTheme.typography.labelMedium.copy(color = TextPrimaryDark, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val categories = BusinessCategory.values().take(4)
                        categories.forEach { cat ->
                            val isSelected = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) GoldPrimary else SurfaceCardDark,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedCategory = cat }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = getCategoryIcon(cat),
                                        contentDescription = null,
                                        tint = if (isSelected) ObsidianDark else TextPrimaryDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = cat.displayName.take(8),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) ObsidianDark else TextPrimaryDark
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Location / Coordinates
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceCardDark,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PinDrop, null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Ubicación Georreferenciada en el Mapa",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                )
                                Text(
                                    text = "Lat: ${String.format("%.4f", lat)}, Lng: ${String.format("%.4f", lng)}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = GoldLight, fontSize = 11.sp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Address
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Dirección Exacta / Vía Principal") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = SurfaceElevatedDark,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Phone & Hours
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Teléfono / WhatsApp") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = SurfaceElevatedDark,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            )
                        )

                        OutlinedTextField(
                            value = openingHours,
                            onValueChange = { openingHours = it },
                            label = { Text("Horario de Atención") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = SurfaceElevatedDark,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Website URL Link (User requested: "link para su pagina webb")
                    OutlinedTextField(
                        value = websiteUrl,
                        onValueChange = { websiteUrl = it },
                        label = { Text("Página Web / Red Social (Opcional)") },
                        placeholder = { Text("https://mi-negocio.com o instagram.com/negocio") },
                        leadingIcon = { Icon(Icons.Default.Language, null, tint = GoldPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = SurfaceElevatedDark,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Business Image Gallery & Photo Picker (From Gallery, File, or Web)
                    BusinessImageGalleryPicker(
                        photoList = photoList,
                        coverPhotoUrl = coverPhotoUrl,
                        onPhotoListChange = { photoList = it },
                        onCoverPhotoChange = { coverPhotoUrl = it }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Open / Closed Initial Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isOpenInitially) "Estado Inicial: Abierto Ahora" else "Estado Inicial: Local Cerrado",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isOpenInitially) EmeraldTrust else RubyAlert,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Switch(
                            checked = isOpenInitially,
                            onCheckedChange = { isOpenInitially = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ObsidianDark,
                                checkedTrackColor = EmeraldTrust
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción comercial") },
                        placeholder = { Text("Detalla los servicios, carta o productos que ofrece.") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = SurfaceElevatedDark,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Products & Prices (User requested: "preio de las cosas que se vende")
                    Text(
                        text = "Productos y Precios que se Venden:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = GoldLight)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Product 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = prod1Name,
                            onValueChange = { prod1Name = it },
                            label = { Text("Producto 1") },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                        )
                        OutlinedTextField(
                            value = prod1Price,
                            onValueChange = { prod1Price = it },
                            label = { Text("Precio S/") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Product 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = prod2Name,
                            onValueChange = { prod2Name = it },
                            label = { Text("Producto 2") },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                        )
                        OutlinedTextField(
                            value = prod2Price,
                            onValueChange = { prod2Price = it },
                            label = { Text("Precio S/") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Promotions (User requested: "sus promos")
                    Text(
                        text = "Promociones Vigentes (Opcional):",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = GoldLight)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = promoTitle,
                            onValueChange = { promoTitle = it },
                            label = { Text("Título de Promoción") },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                        )
                        OutlinedTextField(
                            value = promoCode,
                            onValueChange = { promoCode = it },
                            label = { Text("Código") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Legal Documents (User explicitly requested: "que el ruc y su licencia no sean obligatorias pero que si este esa funcion")
                    Text(
                        text = "Documentación Legal (Función Opcional):",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = GoldLight)
                    )
                    Text(
                        text = "Puedes registrar estos documentos ahora o agregarlos después.",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 11.sp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = rucNumber,
                        onValueChange = { rucNumber = it },
                        label = { Text("RUC Tributario (Opcional)") },
                        placeholder = { Text("11 dígitos (si cuenta con RUC)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = licenseNumber,
                        onValueChange = { licenseNumber = it },
                        label = { Text("Licencia Municipal (Opcional)") },
                        placeholder = { Text("Nº expediente municipal (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Youth Job Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = hasYouthJob,
                            onCheckedChange = { hasYouthJob = it },
                            colors = CheckboxDefaults.colors(checkedColor = EmeraldTrust)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ofrece vacante para jóvenes sin experiencia previa",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Immediate Publish Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceCardDark, RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Checkbox(
                            checked = publishImmediately,
                            onCheckedChange = { publishImmediately = it },
                            colors = CheckboxDefaults.colors(checkedColor = GoldPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Publicar de inmediato en el mapa interactivo",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = GoldLight)
                            )
                            Text(
                                text = "El negocio aparecerá con sus productos, promos y ubicación exacta",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 10.sp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar", color = TextSecondaryDark)
                        }

                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    val price1 = prod1Price.toDoubleOrNull() ?: 25.0
                                    val price2 = prod2Price.toDoubleOrNull() ?: 45.0
                                    val newBusinessId = "biz_custom_${UUID.randomUUID().toString().take(6)}"

                                    val initialMenuItems = listOf(
                                        MenuItem(
                                            id = "m_${UUID.randomUUID().toString().take(6)}",
                                            businessId = newBusinessId,
                                            name = prod1Name.ifBlank { "Producto Principal" },
                                            description = prod1Desc,
                                            price = price1,
                                            category = "Catálogo",
                                            isAvailable = true,
                                            badge = "Popular"
                                        ),
                                        MenuItem(
                                            id = "m_${UUID.randomUUID().toString().take(6)}",
                                            businessId = newBusinessId,
                                            name = prod2Name.ifBlank { "Producto Secundario" },
                                            description = prod2Desc,
                                            price = price2,
                                            category = "Catálogo",
                                            isAvailable = true
                                        )
                                    )

                                    val initialPromos = if (hasPromo && promoTitle.isNotBlank()) {
                                        listOf(
                                            Promotion(
                                                id = "p_${UUID.randomUUID().toString().take(6)}",
                                                businessId = newBusinessId,
                                                title = promoTitle,
                                                description = "Promoción de apertura válida desde la app",
                                                discountPercent = promoDiscount.toIntOrNull() ?: 20,
                                                promoCode = promoCode.ifBlank { "PROMO20" },
                                                expiresText = promoExpires,
                                                isExclusive = true
                                            )
                                        )
                                    } else emptyList()

                                    // Documents are optional
                                    val initialDocs = mutableListOf<VerificationDocument>()
                                    if (rucNumber.isNotBlank()) {
                                        initialDocs.add(
                                            VerificationDocument(
                                                id = "doc_ruc_${UUID.randomUUID().toString().take(4)}",
                                                documentType = DocumentType.RUC_TRIBUTARIO,
                                                documentNumber = rucNumber,
                                                fileName = "RUC_Registrado.pdf",
                                                uploadDate = "Hoy",
                                                isApproved = true,
                                                verificationNotes = "RUC ingresado"
                                            )
                                        )
                                    }
                                    if (licenseNumber.isNotBlank()) {
                                        initialDocs.add(
                                            VerificationDocument(
                                                id = "doc_lic_${UUID.randomUUID().toString().take(4)}",
                                                documentType = DocumentType.LICENCIA_MUNICIPAL,
                                                documentNumber = licenseNumber,
                                                fileName = "Licencia_Municipal.pdf",
                                                uploadDate = "Hoy",
                                                isApproved = true,
                                                verificationNotes = "Licencia ingresada"
                                            )
                                        )
                                    }

                                    val createdBiz = Business(
                                        id = newBusinessId,
                                        name = name.trim(),
                                        category = selectedCategory,
                                        description = description.ifBlank { "Comercio verificado con productos y servicios de alta calidad." },
                                        rating = 5.0,
                                        reviewCount = 1,
                                        address = address.ifBlank { "Av. Principal 500" },
                                        latitude = lat,
                                        longitude = lng,
                                        phone = phone,
                                        openingHours = openingHours,
                                        websiteUrl = websiteUrl.ifBlank { null },
                                        photoUrls = if (photoList.isNotEmpty()) photoList else if (coverPhotoUrl.isNotBlank()) listOf(coverPhotoUrl) else emptyList(),
                                        coverPhotoUrl = if (coverPhotoUrl.isNotBlank()) coverPhotoUrl else photoList.firstOrNull(),
                                        isOpen = isOpenInitially,
                                        isVisibleInApp = true,
                                        verificationStatus = if (publishImmediately) VerificationStatus.VERIFICADO_PUBLICADO else VerificationStatus.PENDIENTE_REVISION,
                                        reputationScore = 98,
                                        reputationVerdict = "Comercio Registrado y Verificado por Admin",
                                        verifiedByAdminDate = "Hoy",
                                        documents = initialDocs,
                                        menuItems = initialMenuItems,
                                        promotions = initialPromos,
                                        ownerName = "Administrador Registrado",
                                        ownerEmail = "admin@urbe.com"
                                    )

                                    onSaveAndPublish(createdBiz)
                                }
                            },
                            enabled = name.isNotBlank(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = ObsidianDark
                            ),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(48.dp)
                                .testTag("save_and_publish_business_button")
                        ) {
                            Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Guardar y Publicar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
