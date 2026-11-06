package com.example.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.model.AppNotification
import com.example.model.NotificationType
import com.example.ui.theme.*

@Composable
fun NotificationsScreen(
    notifications: List<AppNotification>,
    onMarkAsRead: (String) -> Unit,
    onMarkAllAsRead: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf<NotificationType?>(null) }

    val filteredList = remember(notifications, selectedFilter) {
        if (selectedFilter == null) notifications else notifications.filter { it.type == selectedFilter }
    }

    Scaffold(
        containerColor = ObsidianDark
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Centro de Avisos & Promos",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                        Text(
                            text = "Alertas en tiempo real de comercios y ofertas",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                        )
                    }

                    TextButton(
                        onClick = onMarkAllAsRead,
                        modifier = Modifier.testTag("mark_all_read_button")
                    ) {
                        Text("Marcar leídas", color = GoldLight, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Filter row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text("Todas", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = ObsidianDark
                        )
                    )
                    FilterChip(
                        selected = selectedFilter == NotificationType.OFERTA_EXCLUSIVA,
                        onClick = { selectedFilter = NotificationType.OFERTA_EXCLUSIVA },
                        label = { Text("Ofertas", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = ObsidianDark
                        )
                    )
                    FilterChip(
                        selected = selectedFilter == NotificationType.ESTADO_NEGOCIO,
                        onClick = { selectedFilter = NotificationType.ESTADO_NEGOCIO },
                        label = { Text("Estados", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldTrust,
                            selectedLabelColor = ObsidianDark
                        )
                    )
                    FilterChip(
                        selected = selectedFilter == NotificationType.EMPLEO_JOVEN,
                        onClick = { selectedFilter = NotificationType.EMPLEO_JOVEN },
                        label = { Text("Empleo", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CobaltBlue,
                            selectedLabelColor = ObsidianDark
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay notificaciones en esta categoría.", color = TextSecondaryDark)
                    }
                }
            } else {
                items(filteredList, key = { it.id }) { notif ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (notif.isRead) SlateNavyDark else SurfaceCardDark,
                        border = if (!notif.isRead) androidx.compose.foundation.BorderStroke(1.dp, GoldContainer) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onMarkAsRead(notif.id) }
                            .testTag("notification_card_${notif.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(notif.type.badgeColor).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (notif.type) {
                                        NotificationType.OFERTA_EXCLUSIVA -> Icons.Default.LocalOffer
                                        NotificationType.ESTADO_NEGOCIO -> Icons.Default.Storefront
                                        NotificationType.PEDIDO_ACTUALIZACION -> Icons.Default.DeliveryDining
                                        NotificationType.EMPLEO_JOVEN -> Icons.Default.Work
                                        NotificationType.SISTEMA_ADMIN -> Icons.Default.Shield
                                    },
                                    contentDescription = null,
                                    tint = Color(notif.type.badgeColor),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.Medium,
                                            color = TextPrimaryDark
                                        )
                                    )
                                    Text(
                                        text = notif.timestamp,
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 10.sp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = notif.message,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (!notif.isRead) TextPrimaryDark else TextSecondaryDark,
                                        lineHeight = 16.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
