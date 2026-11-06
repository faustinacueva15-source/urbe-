package com.example.ui.orders

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.ui.theme.*

@Composable
fun OrdersTrackingScreen(
    orders: List<Order>,
    onAdvanceOrderStatus: (String) -> Unit,
    onNavigateToMap: () -> Unit
) {
    Scaffold(
        containerColor = ObsidianDark
    ) { paddingValues ->
        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(GoldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Aún no tienes pedidos",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Explora los comercios verificados en el mapa y realiza tu primer pedido con entrega segura.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onNavigateToMap,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = ObsidianDark
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("explore_map_from_empty_orders_button")
                    ) {
                        Icon(Icons.Default.Map, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Explorar Negocios en el Mapa", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Text(
                        text = "Mis Pedidos & Seguimiento en Vivo",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )
                    Text(
                        text = "Control en tiempo real de tus compras verificadas",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(orders, key = { it.id }) { order ->
                    OrderTrackingCard(
                        order = order,
                        onAdvanceStatus = { onAdvanceOrderStatus(order.id) }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
fun OrderTrackingCard(
    order: Order,
    onAdvanceStatus: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = SlateNavyDark,
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_card_${order.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Business Name & Order ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.businessName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )
                    Text(
                        text = "Orden #${order.id} • ${order.createdAt}",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when (order.status) {
                        OrderStatus.RECIBIDO -> GoldContainer
                        OrderStatus.EN_PREPARACION -> Color(0xFF1E3A5F)
                        OrderStatus.EN_CAMINO -> EmeraldContainer
                        OrderStatus.ENTREGADO -> SurfaceCardDark
                    }
                ) {
                    Text(
                        text = order.status.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (order.status) {
                                OrderStatus.RECIBIDO -> OnGoldContainer
                                OrderStatus.EN_PREPARACION -> CobaltBlue
                                OrderStatus.EN_CAMINO -> OnEmeraldContainer
                                OrderStatus.ENTREGADO -> TextSecondaryDark
                            }
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Progress Timeline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val steps = OrderStatus.values()
                steps.forEachIndexed { index, step ->
                    val isCompleted = step.stepIndex <= order.status.stepIndex
                    val isCurrent = step == order.status

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCurrent -> GoldPrimary
                                        isCompleted -> EmeraldTrust
                                        else -> SurfaceCardDark
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.Check
                                    1 -> Icons.Default.OutdoorGrill
                                    2 -> Icons.Default.DeliveryDining
                                    else -> Icons.Default.Home
                                },
                                contentDescription = null,
                                tint = if (isCompleted || isCurrent) ObsidianDark else TextSecondaryDark,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = when (index) {
                                0 -> "Validado"
                                1 -> "Cocina"
                                2 -> "En Ruta"
                                else -> "Entregado"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrent) GoldLight else TextSecondaryDark
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Items list
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceCardDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.quantity}x ${item.menuItem.name}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark)
                            )
                            Text(
                                text = "S/ ${String.format("%.2f", item.lineTotal)}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                            )
                        }
                    }

                    Divider(color = SurfaceElevatedDark, modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Pagado (${order.paymentMethod.label})",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                        )
                        Text(
                            text = "S/ ${String.format("%.2f", order.total)}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Delivery address
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = order.deliveryAddress,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                )
            }

            // Interactive simulation button for the user to advance the order lifecycle
            if (order.status != OrderStatus.ENTREGADO) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onAdvanceStatus,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = SurfaceCardDark,
                        contentColor = GoldPrimary
                    ),
                    border = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("advance_order_status_button_${order.id}")
                ) {
                    Icon(Icons.Default.FastForward, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Simular Avance de Estado (${when (order.status) {
                            OrderStatus.RECIBIDO -> "A Cocina"
                            OrderStatus.EN_PREPARACION -> "A Repartidor en Ruta"
                            OrderStatus.EN_CAMINO -> "Marcar Entregado"
                            OrderStatus.ENTREGADO -> ""
                        }})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
