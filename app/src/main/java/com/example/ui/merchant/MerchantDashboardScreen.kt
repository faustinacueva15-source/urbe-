package com.example.ui.merchant

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
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun MerchantDashboardScreen(
    businesses: List<Business>,
    orders: List<Order>,
    jobOffers: List<JobOffer>,
    onToggleOpenClose: (String) -> Unit,
    onToggleItemAvailability: (businessId: String, itemId: String) -> Unit,
    onAddMenuItem: (businessId: String, item: MenuItem) -> Unit,
    onPostJobOffer: (JobOffer) -> Unit
) {
    // Current merchant can switch between businesses they manage
    var selectedBizIndex by remember { mutableIntStateOf(0) }
    val currentBiz = businesses.getOrNull(selectedBizIndex) ?: businesses.firstOrNull()

    var showAddItemDialog by remember { mutableStateOf(false) }
    var showPostJobDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ObsidianDark
    ) { paddingValues ->
        if (currentBiz == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay negocios asociados a esta cuenta.", color = TextSecondaryDark)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp)
            ) {
                // Header & Business Switcher
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SlateNavyDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldTrust),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Storefront,
                                            contentDescription = null,
                                            tint = ObsidianDark,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = currentBiz.name,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimaryDark
                                            )
                                        )
                                        Text(
                                            text = "Panel de Propietario • ${currentBiz.category.displayName}",
                                            style = MaterialTheme.typography.labelSmall.copy(color = EmeraldTrust)
                                        )
                                    }
                                }

                                // Open / Closed quick switch
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Switch(
                                        checked = currentBiz.isOpen,
                                        onCheckedChange = { onToggleOpenClose(currentBiz.id) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = ObsidianDark,
                                            checkedTrackColor = EmeraldTrust
                                        ),
                                        modifier = Modifier.testTag("merchant_toggle_open_close")
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (currentBiz.isOpen) "Abierto" else "Cerrado",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (currentBiz.isOpen) EmeraldTrust else RubyAlert,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sales Metrics Cards
                    Text(
                        text = "Ventas & Reportes en Tiempo Real",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SlateNavyDark,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Ventas Hoy", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                                Text("S/ 840.00", style = MaterialTheme.typography.titleLarge.copy(color = GoldLight, fontWeight = FontWeight.Bold))
                                Text("+18% vs ayer", style = MaterialTheme.typography.labelSmall.copy(color = EmeraldTrust, fontSize = 10.sp))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SlateNavyDark,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Pedidos Atendidos", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                                Text("18 órdenes", style = MaterialTheme.typography.titleLarge.copy(color = TextPrimaryDark, fontWeight = FontWeight.Bold))
                                Text("Ticket prom. S/ 46.60", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 10.sp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Menu & Catalogue Management Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Gestión de Menú & Productos",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                            )
                            Text(
                                text = "${currentBiz.menuItems.size} ítems registrados",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                            )
                        }

                        Button(
                            onClick = { showAddItemDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = ObsidianDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("merchant_add_product_button")
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nuevo Producto", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Menu items
                items(currentBiz.menuItems, key = { it.id }) { item ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SlateNavyDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                )
                                Text(
                                    text = "S/ ${String.format("%.2f", item.price)} • ${item.category}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = GoldLight)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (item.isAvailable) "Disponible" else "Agotado",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (item.isAvailable) EmeraldTrust else RubyAlert
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = item.isAvailable,
                                    onCheckedChange = { onToggleItemAvailability(currentBiz.id, item.id) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = ObsidianDark,
                                        checkedTrackColor = EmeraldTrust
                                    )
                                )
                            }
                        }
                    }
                }

                // Youth Job Posting Section
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Bolsa de Empleo del Negocio",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                            )
                            Text(
                                text = "Genera empleo para jóvenes sin experiencia previa",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                            )
                        }

                        Button(
                            onClick = { showPostJobDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldTrust,
                                contentColor = ObsidianDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("merchant_post_job_button")
                        ) {
                            Icon(Icons.Default.PostAdd, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Publicar Vacante", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                val bizJobs = jobOffers.filter { it.businessId == currentBiz.id }
                if (bizJobs.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SlateNavyDark,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Aún no has publicado avisos de empleo para este local. ¡Genera oportunidades laborales juveniles!",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark),
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                } else {
                    items(bizJobs, key = { it.id }) { job ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SlateNavyDark,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = job.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GoldLight
                                    )
                                )
                                Text(
                                    text = "${job.employmentType} • ${job.salaryMonthly} • Sin experiencia previa",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Add Menu Item
    if (showAddItemDialog && currentBiz != null) {
        var itemName by remember { mutableStateOf("") }
        var itemDesc by remember { mutableStateOf("") }
        var itemPrice by remember { mutableStateOf("25.00") }
        var itemCategory by remember { mutableStateOf("Especialidades") }

        Dialog(onDismissRequest = { showAddItemDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SlateNavyDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Agregar Producto o Servicio",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Nombre del ítem") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = itemDesc,
                        onValueChange = { itemDesc = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = itemPrice,
                        onValueChange = { itemPrice = it },
                        label = { Text("Precio (S/)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddItemDialog = false },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar", color = TextSecondaryDark)
                        }

                        Button(
                            onClick = {
                                val priceVal = itemPrice.toDoubleOrNull() ?: 20.0
                                val newItem = MenuItem(
                                    id = "m_${UUID.randomUUID().toString().take(6)}",
                                    businessId = currentBiz.id,
                                    name = itemName,
                                    description = itemDesc,
                                    price = priceVal,
                                    category = itemCategory,
                                    isAvailable = true
                                )
                                onAddMenuItem(currentBiz.id, newItem)
                                showAddItemDialog = false
                            },
                            enabled = itemName.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianDark),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Guardar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Modal: Post Youth Job Offer
    if (showPostJobDialog && currentBiz != null) {
        var jobTitle by remember { mutableStateOf("") }
        var jobDesc by remember { mutableStateOf("") }
        var jobSalary by remember { mutableStateOf("S/ 850 + Capacitación") }
        var jobSchedule by remember { mutableStateOf("Medio Tiempo (Tardes)") }

        Dialog(onDismissRequest = { showPostJobDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SlateNavyDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Publicar Empleo Juvenil",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldTrust
                        )
                    )
                    Text(
                        text = "Aviso orientado a jóvenes sin experiencia previa",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = jobTitle,
                        onValueChange = { jobTitle = it },
                        label = { Text("Título del Puesto (ej. Ayudante, Aprendiz)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = jobDesc,
                        onValueChange = { jobDesc = it },
                        label = { Text("Descripción de tareas y entrenamiento") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = jobSalary,
                        onValueChange = { jobSalary = it },
                        label = { Text("Remuneración / Beneficios") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showPostJobDialog = false },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar", color = TextSecondaryDark)
                        }

                        Button(
                            onClick = {
                                val newJob = JobOffer(
                                    id = "job_${UUID.randomUUID().toString().take(6)}",
                                    businessId = currentBiz.id,
                                    businessName = currentBiz.name,
                                    businessCategory = currentBiz.category,
                                    title = jobTitle,
                                    description = jobDesc.ifBlank { "Buscamos jóvenes motivados para integrarse a nuestro equipo. Te capacitamos desde el primer día." },
                                    isYouthNoExperience = true,
                                    employmentType = jobSchedule,
                                    salaryMonthly = jobSalary,
                                    workSchedule = "Lunes a Viernes",
                                    address = currentBiz.address,
                                    requirements = listOf("Sin experiencia previa", "Puntualidad y ganas de aprender", "Mayor de 18 años"),
                                    benefits = listOf("Capacitación incluida", "Descuentos en tienda", "Buen clima laboral")
                                )
                                onPostJobOffer(newJob)
                                showPostJobDialog = false
                            },
                            enabled = jobTitle.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldTrust, contentColor = ObsidianDark),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Publicar Aviso", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
