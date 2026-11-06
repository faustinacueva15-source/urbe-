package com.example.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun MapFilterDialog(
    onlyOpen: Boolean,
    onToggleOnlyOpen: () -> Unit,
    onlyPromos: Boolean,
    onToggleOnlyPromos: () -> Unit,
    minRating: Double,
    onMinRatingChange: (Double) -> Unit,
    maxDistanceKm: Float,
    onMaxDistanceChange: (Float) -> Unit,
    onDismiss: () -> Unit,
    onReset: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SlateNavyDark,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("map_filter_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Filtros de Exploración",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                )
                Text(
                    text = "Personaliza los negocios visibles en tu zona",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Distance / Radius slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Radio de Proximidad",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = TextPrimaryDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "${String.format("%.1f", maxDistanceKm)} km",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Slider(
                    value = maxDistanceKm,
                    onValueChange = onMaxDistanceChange,
                    valueRange = 0.5f..15.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = GoldPrimary,
                        activeTrackColor = GoldPrimary,
                        inactiveTrackColor = SurfaceElevatedDark
                    ),
                    modifier = Modifier.testTag("distance_slider")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Rating filter
                Text(
                    text = "Valoración Mínima de Usuarios",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val ratings = listOf(0.0 to "Todas", 4.0 to "4.0+ ★", 4.5 to "4.5+ ★", 4.8 to "4.8+ ★")
                    ratings.forEach { (value, label) ->
                        val isSelected = minRating == value
                        OutlinedButton(
                            onClick = { onMinRatingChange(value) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) GoldPrimary else SurfaceCardDark,
                                contentColor = if (isSelected) ObsidianDark else TextPrimaryDark
                            ),
                            border = null,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("rating_filter_${value.toInt()}")
                        ) {
                            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Toggle: Solo Abiertos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Solo Negocios Abiertos Ahora",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimaryDark,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "Ocultar comercios que actualmente están cerrados",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark, fontSize = 11.sp)
                        )
                    }
                    Switch(
                        checked = onlyOpen,
                        onCheckedChange = { onToggleOnlyOpen() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ObsidianDark,
                            checkedTrackColor = EmeraldTrust,
                            uncheckedThumbColor = TextSecondaryDark,
                            uncheckedTrackColor = SurfaceElevatedDark
                        ),
                        modifier = Modifier.testTag("filter_switch_open")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle: Solo con Promociones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Solo con Promociones Exclusivas",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimaryDark,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "Destacar ofertas 2x1 y descuentos vigentes",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark, fontSize = 11.sp)
                        )
                    }
                    Switch(
                        checked = onlyPromos,
                        onCheckedChange = { onToggleOnlyPromos() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ObsidianDark,
                            checkedTrackColor = GoldPrimary,
                            uncheckedThumbColor = TextSecondaryDark,
                            uncheckedTrackColor = SurfaceElevatedDark
                        ),
                        modifier = Modifier.testTag("filter_switch_promos")
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReset,
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("reset_filters_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Restablecer", color = TextSecondaryDark)
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = ObsidianDark
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("apply_filters_button")
                    ) {
                        Text("Aplicar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
