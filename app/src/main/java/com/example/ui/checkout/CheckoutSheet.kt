package com.example.ui.checkout

import androidx.compose.animation.AnimatedVisibility
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
import com.example.model.OrderItem
import com.example.model.PaymentMethod
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutSheet(
    cartItems: List<OrderItem>,
    subtotal: Double,
    onAddToCart: (com.example.model.MenuItem, String) -> Unit,
    onRemoveFromCart: (String) -> Unit,
    onProcessOrder: (paymentMethod: PaymentMethod, address: String, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CONTRAENTREGA_EFECTIVO) }
    var deliveryAddress by remember { mutableStateOf("Av. Del Parque 450, Dpto 402, Miraflores") }
    var customerNotes by remember { mutableStateOf("") }

    // Card form state
    var cardNumber by remember { mutableStateOf("4532 •••• •••• 8891") }
    var cardHolder by remember { mutableStateOf("JULIAN MORALES") }
    var cardExpiry by remember { mutableStateOf("08/29") }

    var isProcessing by remember { mutableStateOf(false) }

    val deliveryFee = 5.0
    val promoDiscount = 0.0
    val total = (subtotal + deliveryFee - promoDiscount).coerceAtLeast(0.0)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SlateNavyDark,
        dragHandle = {
            Surface(
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
                color = SurfaceElevatedDark,
                shape = CircleShape
            ) {
                Box(modifier = Modifier.size(width = 36.dp, height = 4.dp))
            }
        },
        modifier = Modifier.testTag("checkout_modal_bottom_sheet")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pasarela de Pago Segura",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, null, tint = EmeraldTrust, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Contraentrega, Yape, Transferencia o Tarjeta",
                                style = MaterialTheme.typography.labelSmall.copy(color = EmeraldTrust)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("close_checkout_sheet_button")
                    ) {
                        Icon(Icons.Default.Close, null, tint = TextSecondaryDark)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Order Summary
                Text(
                    text = "Resumen del Pedido",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(cartItems, key = { it.menuItem.id }) { item ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceCardDark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.menuItem.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                            )
                            Text(
                                text = "S/ ${String.format("%.2f", item.menuItem.price)} c/u",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onRemoveFromCart(item.menuItem.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Remove, null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                            }
                            Text(
                                text = "${item.quantity}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            IconButton(
                                onClick = { onAddToCart(item.menuItem, item.menuItem.businessId) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Add, null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "S/ ${String.format("%.2f", item.lineTotal)}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Delivery Address
                Text(
                    text = "Dirección de Entrega",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = deliveryAddress,
                    onValueChange = { deliveryAddress = it },
                    leadingIcon = {
                        Icon(Icons.Default.Place, null, tint = GoldPrimary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("delivery_address_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = SurfaceElevatedDark,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customerNotes,
                    onValueChange = { customerNotes = it },
                    placeholder = { Text("Instrucciones para el repartidor (ej. pagaré con billete de S/ 100)", color = TextSecondaryDark, fontSize = 12.sp) },
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

                // Section 3: Payment Method
                Text(
                    text = "Selecciona Método de Pago",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                PaymentMethod.values().forEach { method ->
                    val isSelected = selectedPaymentMethod == method
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) SurfaceCardDark else SlateNavyDark,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isSelected) GoldPrimary else SurfaceElevatedDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedPaymentMethod = method }
                            .testTag("payment_method_${method.name.lowercase()}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedPaymentMethod = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = method.label,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimaryDark
                                        )
                                    )
                                    Text(
                                        text = method.description,
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                                    )
                                }
                                Icon(
                                    imageVector = when (method) {
                                        PaymentMethod.CONTRAENTREGA_EFECTIVO -> Icons.Default.Payments
                                        PaymentMethod.YAPE_BILLETERA -> Icons.Default.QrCode2
                                        PaymentMethod.TRANSFERENCIA_BANCARIA -> Icons.Default.AccountBalance
                                        PaymentMethod.TARJETA -> Icons.Default.CreditCard
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) GoldPrimary else TextSecondaryDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Expanded detail instructions when selected
                            if (isSelected) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = SlateNavyDark,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Info, null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = method.instructions,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = GoldLight,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // If Credit/Debit card selected: Show card details preview
                AnimatedVisibility(visible = selectedPaymentMethod == PaymentMethod.TARJETA) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceCardDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Tarjeta Protegida TLS 256-Bit", style = MaterialTheme.typography.labelSmall.copy(color = GoldLight))
                                Icon(Icons.Default.Lock, null, tint = EmeraldTrust, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(cardNumber, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimaryDark, letterSpacing = 2.sp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("TITULAR: $cardHolder", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                                Text("VENCE: $cardExpiry", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Price Breakdown
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceCardDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal de productos", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark))
                            Text("S/ ${String.format("%.2f", subtotal)}", style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Costo de Envío", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark))
                            Text("S/ ${String.format("%.2f", deliveryFee)}", style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark))
                        }
                        Divider(color = SurfaceElevatedDark, modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total a Pagar", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimaryDark))
                            Text("S/ ${String.format("%.2f", total)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = GoldLight))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Order Button
                Button(
                    onClick = {
                        isProcessing = true
                        onProcessOrder(selectedPaymentMethod, deliveryAddress, customerNotes)
                    },
                    enabled = !isProcessing && cartItems.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = ObsidianDark
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_order_button")
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(color = ObsidianDark, modifier = Modifier.size(22.dp))
                    } else {
                        Icon(
                            imageVector = when (selectedPaymentMethod) {
                                PaymentMethod.CONTRAENTREGA_EFECTIVO -> Icons.Default.Payments
                                PaymentMethod.YAPE_BILLETERA -> Icons.Default.QrCode2
                                PaymentMethod.TRANSFERENCIA_BANCARIA -> Icons.Default.AccountBalance
                                PaymentMethod.TARJETA -> Icons.Default.Lock
                            },
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Confirmar Pedido (S/ ${String.format("%.2f", total)})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
