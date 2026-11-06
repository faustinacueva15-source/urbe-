package com.example.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.components.VerificationBadge
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun AdminPanelScreen(
    businesses: List<Business>,
    inspectingBusiness: Business?,
    showAddBusinessDialog: Boolean,
    onInspectBusiness: (Business?) -> Unit,
    onApproveVerification: (businessId: String, score: Int, verdict: String) -> Unit,
    onRejectVerification: (businessId: String, reason: String) -> Unit,
    onToggleVisibility: (String) -> Unit,
    onToggleOpenClose: (String) -> Unit,
    onOpenAddBusinessDialog: () -> Unit,
    onCloseAddBusinessDialog: () -> Unit,
    onAddNewBusiness: (Business) -> Unit
) {
    var adminTab by remember { mutableIntStateOf(0) } // 0: Auditoría de Documentos, 1: Control en Tiempo Real, 2: Métricas

    val pendingBusinesses = remember(businesses) {
        businesses.filter { it.verificationStatus != VerificationStatus.VERIFICADO_PUBLICADO }
    }

    val verifiedBusinesses = remember(businesses) {
        businesses.filter { it.verificationStatus == VerificationStatus.VERIFICADO_PUBLICADO }
    }

    Scaffold(
        containerColor = ObsidianDark,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onOpenAddBusinessDialog,
                containerColor = GoldPrimary,
                contentColor = ObsidianDark,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.AddBusiness, null) },
                text = { Text("Registrar Negocio", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("admin_add_business_fab")
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Header Banner
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SlateNavyDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = ObsidianDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Panel de Control Administrativo",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GoldLight
                                    )
                                )
                                Text(
                                    text = "Verificación única de documentos y control de visibilidad",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Garantía de confianza: Solo los negocios verificados manualmente por el Administrador son publicados en el mapa público. Gestiona sus estados y visibilidad en tiempo real.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, lineHeight = 17.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Admin Tabs Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { adminTab = 0 },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (adminTab == 0) GoldPrimary else SurfaceCardDark,
                            contentColor = if (adminTab == 0) ObsidianDark else TextPrimaryDark
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_tab_audit")
                    ) {
                        Text("Auditoría (${pendingBusinesses.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { adminTab = 1 },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (adminTab == 1) GoldPrimary else SurfaceCardDark,
                            contentColor = if (adminTab == 1) ObsidianDark else TextPrimaryDark
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_tab_realtime")
                    ) {
                        Text("Negocios (${businesses.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { adminTab = 2 },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (adminTab == 2) GoldPrimary else SurfaceCardDark,
                            contentColor = if (adminTab == 2) ObsidianDark else TextPrimaryDark
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_tab_metrics")
                    ) {
                        Text("Métricas", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Tab 0: Auditoría y Verificación de Documentos
            if (adminTab == 0) {
                if (pendingBusinesses.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SlateNavyDark,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.VerifiedUser, null, tint = EmeraldTrust, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "¡Al día! No hay negocios pendientes de verificación",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                )
                                Text(
                                    text = "Todos los comercios activos cuentan con documentación legal y reputación auditada.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "Comercios en Espera de Verificación Legal",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(pendingBusinesses, key = { it.id }) { biz ->
                        AdminPendingBusinessCard(
                            business = biz,
                            onInspectClick = { onInspectBusiness(biz) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            } else if (adminTab == 1) {
                // Tab 1: Real-Time Business & Visibility Management
                item {
                    Text(
                        text = "Gestión de Estados y Visibilidad en Tiempo Real",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )
                    Text(
                        text = "Controla qué negocios se publican en el mapa y sus horarios de apertura",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(businesses, key = { it.id }) { biz ->
                    AdminRealtimeBusinessCard(
                        business = biz,
                        onToggleVisibility = { onToggleVisibility(biz.id) },
                        onToggleOpenClose = { onToggleOpenClose(biz.id) },
                        onInspectDocuments = { onInspectBusiness(biz) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                // Tab 2: Ecosystem Metrics
                item {
                    EcosystemMetricsSection(
                        totalBusinesses = businesses.size,
                        verifiedCount = verifiedBusinesses.size,
                        pendingCount = pendingBusinesses.size
                    )
                }
            }
        }
    }

    // Modal Dialog: Document Inspector & Auditor
    if (inspectingBusiness != null) {
        val biz = inspectingBusiness
        var scoreInput by remember { mutableIntStateOf(biz.reputationScore) }
        var verdictNotes by remember { mutableStateOf("Documentación tributaria, municipal y sanitaria verificada conforme a ley.") }

        Dialog(onDismissRequest = { onInspectBusiness(null) }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SlateNavyDark,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("admin_document_inspector_dialog")
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Auditoría de Documentos",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GoldPrimary
                                    )
                                )
                                Text(
                                    text = biz.name,
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                                )
                            }
                            IconButton(onClick = { onInspectBusiness(null) }) {
                                Icon(Icons.Default.Close, null, tint = TextSecondaryDark)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Status Badge
                        VerificationBadge(
                            status = biz.verificationStatus,
                            reputationScore = biz.reputationScore
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Expediente Legal del Comercio:",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Documents List
                    items(biz.documents, key = { it.id }) { doc ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceCardDark,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (doc.isApproved) EmeraldTrust.copy(alpha = 0.5f) else AmberPending.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (doc.isApproved) Icons.Default.CheckCircle else Icons.Default.Description,
                                    contentDescription = null,
                                    tint = if (doc.isApproved) EmeraldTrust else AmberPending,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = doc.documentType.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimaryDark
                                        )
                                    )
                                    Text(
                                        text = "Nº ${doc.documentNumber} • ${doc.documentType.issuingAuthority}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                                    )
                                    Text(
                                        text = "Archivo: ${doc.fileName} (${doc.uploadDate})",
                                        style = MaterialTheme.typography.labelSmall.copy(color = GoldLight, fontSize = 10.sp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(14.dp))

                        // Reputation Score Slider (0 to 100)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Score de Reputación Comercial",
                                style = MaterialTheme.typography.labelMedium.copy(color = TextPrimaryDark, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "$scoreInput / 100",
                                style = MaterialTheme.typography.titleMedium.copy(color = GoldPrimary, fontWeight = FontWeight.Bold)
                            )
                        }

                        Slider(
                            value = scoreInput.toFloat(),
                            onValueChange = { scoreInput = it.toInt() },
                            valueRange = 50f..100f,
                            colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = verdictNotes,
                            onValueChange = { verdictNotes = it },
                            label = { Text("Dictamen de Auditoría Admin") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = SurfaceElevatedDark,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            )
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Actions: Approve & Publish or Reject
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onRejectVerification(biz.id, "Documentos incompletos o pendientes de subsanación")
                                    onInspectBusiness(null)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RubyAlert),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("admin_reject_business_button")
                            ) {
                                Text("Rechazar")
                            }

                            Button(
                                onClick = {
                                    onApproveVerification(biz.id, scoreInput, verdictNotes)
                                    onInspectBusiness(null)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldTrust,
                                    contentColor = ObsidianDark
                                ),
                                modifier = Modifier
                                    .weight(1.4f)
                                    .testTag("admin_approve_and_publish_button")
                            ) {
                                Icon(Icons.Default.Verified, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Aprobar y Publicar", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog: Register New Business by Admin
    if (showAddBusinessDialog) {
        AdminNewBusinessDialog(
            onDismiss = onCloseAddBusinessDialog,
            onAddBusiness = onAddNewBusiness
        )
    }
}

@Composable
fun AdminPendingBusinessCard(
    business: Business,
    onInspectClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SlateNavyDark,
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(GoldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(business.category),
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = business.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                        Text(
                            text = "${business.category.displayName} • Titular: ${business.ownerName}",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldContainer
                ) {
                    Text(
                        text = "Pendiente",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnGoldContainer,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${business.documents.size} documentos legales subidos para revisión (RUC, Licencia, DNI)",
                style = MaterialTheme.typography.bodySmall.copy(color = GoldLight)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onInspectClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = ObsidianDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("inspect_business_button_${business.id}")
            ) {
                Icon(Icons.Default.FactCheck, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Revisar Documentos y Validar Reputación", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AdminRealtimeBusinessCard(
    business: Business,
    onToggleVisibility: () -> Unit,
    onToggleOpenClose: () -> Unit,
    onInspectDocuments: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SlateNavyDark,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = business.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )
                    Text(
                        text = "${business.category.displayName} • ${business.address}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                    )
                }

                VerificationBadge(
                    status = business.verificationStatus,
                    reputationScore = business.reputationScore
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Real-Time Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Control 1: Visibilidad en Plataforma
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = business.isVisibleInApp,
                        onCheckedChange = { onToggleVisibility() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ObsidianDark,
                            checkedTrackColor = GoldPrimary
                        ),
                        modifier = Modifier.testTag("toggle_visibility_${business.id}")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (business.isVisibleInApp) "Visible en Mapa" else "Oculto",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (business.isVisibleInApp) GoldLight else TextSecondaryDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Control 2: Abierto / Cerrado
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = business.isOpen,
                        onCheckedChange = { onToggleOpenClose() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ObsidianDark,
                            checkedTrackColor = EmeraldTrust
                        ),
                        modifier = Modifier.testTag("toggle_open_close_${business.id}")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (business.isOpen) "Abierto" else "Cerrado",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (business.isOpen) EmeraldTrust else RubyAlert,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onInspectDocuments,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimaryDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Verified, null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ver Documentación y Certificados", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun EcosystemMetricsSection(
    totalBusinesses: Int,
    verifiedCount: Int,
    pendingCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Reportes y Métricas del Ecosistema",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SlateNavyDark,
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Registrados", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                    Text("$totalBusinesses", style = MaterialTheme.typography.headlineMedium.copy(color = GoldPrimary, fontWeight = FontWeight.Bold))
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SlateNavyDark,
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("100% Verificados", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                    Text("$verifiedCount", style = MaterialTheme.typography.headlineMedium.copy(color = EmeraldTrust, fontWeight = FontWeight.Bold))
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SlateNavyDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Garantía de Auditoría Urbe",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = GoldLight)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Cada comercio en la red cuenta con validación de identidad fiscal (RUC), licencia de funcionamiento municipal vigente y certificado higiénico-sanitario.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                )
            }
        }
    }
}

@Composable
fun AdminNewBusinessDialog(
    onDismiss: () -> Unit,
    onAddBusiness: (Business) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(BusinessCategory.BARBERIA) }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+51 987 000 111") }
    var hours by remember { mutableStateOf("Lun - Sáb: 09:00 - 20:00") }
    var rucNumber by remember { mutableStateOf("20608899221") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SlateNavyDark,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("admin_add_business_dialog")
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                item {
                    Text(
                        text = "Registrar Nuevo Negocio",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    )
                    Text(
                        text = "Solo el Admin puede crear comercios en la plataforma",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre del Comercio") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Categoría:", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val sampleCategories = listOf(BusinessCategory.BARBERIA, BusinessCategory.REPOSTERIA, BusinessCategory.BODEGA, BusinessCategory.CAFETERIA)
                        sampleCategories.forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat.displayName, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = ObsidianDark
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción del negocio") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Dirección completa") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = rucNumber,
                        onValueChange = { rucNumber = it },
                        label = { Text("Número de RUC / Registro Tributario") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

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
                                if (name.isNotBlank()) {
                                    val newBiz = Business(
                                        id = "biz_${UUID.randomUUID().toString().take(6)}",
                                        name = name,
                                        category = category,
                                        description = description.ifBlank { "Comercio verificado en la plataforma Urbe." },
                                        rating = 5.0,
                                        reviewCount = 1,
                                        address = address.ifBlank { "Av. Principal 123" },
                                        latitude = -12.1220,
                                        longitude = -77.0305,
                                        phone = phone,
                                        openingHours = hours,
                                        isOpen = true,
                                        isVisibleInApp = true,
                                        verificationStatus = VerificationStatus.VERIFICADO_PUBLICADO,
                                        reputationScore = 97,
                                        reputationVerdict = "Validado y Aprobado por Administración",
                                        verifiedByAdminDate = "05 Nov 2026",
                                        documents = listOf(
                                            VerificationDocument("doc_auto_1", DocumentType.RUC_TRIBUTARIO, rucNumber, "Certificado_RUC.pdf", "05 Nov 2026", true),
                                            VerificationDocument("doc_auto_2", DocumentType.LICENCIA_MUNICIPAL, "LIC-2026-90", "Licencia_Municipal.pdf", "05 Nov 2026", true)
                                        ),
                                        menuItems = listOf(
                                            MenuItem("m_auto_1", "biz_new", "Servicio o Producto Principal", "Elaborado con los más altos estándares de calidad", 30.0, "General", true)
                                        )
                                    )
                                    onAddBusiness(newBiz)
                                }
                            },
                            enabled = name.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianDark),
                            modifier = Modifier.weight(1f).testTag("save_new_business_button")
                        ) {
                            Text("Crear y Validar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
