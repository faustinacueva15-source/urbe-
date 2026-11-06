package com.example.ui.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BusinessCategory
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.*

@Composable
fun MapSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedCategory: BusinessCategory?,
    onSelectCategory: (BusinessCategory?) -> Unit,
    onlyOpen: Boolean,
    onToggleOnlyOpen: () -> Unit,
    minRating: Double,
    maxDistanceKm: Float,
    onOpenFilterDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Search Input Field
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SlateNavyDark,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("map_search_bar_surface")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = GoldPrimary,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = {
                        Text(
                            text = "Buscar barberías, reposterías, bodegas...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondaryDark,
                                fontSize = 13.sp
                            )
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        disabledContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("map_search_input_field")
                )

                AnimatedVisibility(visible = query.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("clear_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Limpiar búsqueda",
                            tint = TextSecondaryDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Filter button
                val hasActiveFilters = onlyOpen || minRating > 0.0 || maxDistanceKm < 10.0f
                IconButton(
                    onClick = onOpenFilterDialog,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("open_filter_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (hasActiveFilters) {
                                Badge(
                                    containerColor = GoldPrimary,
                                    modifier = Modifier.size(8.dp)
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filtros",
                            tint = if (hasActiveFilters) GoldPrimary else TextPrimaryDark
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "Todos" Chip
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onSelectCategory(null) },
                label = { Text("Todos", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AllInclusive,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GoldPrimary,
                    selectedLabelColor = ObsidianDark,
                    selectedLeadingIconColor = ObsidianDark,
                    containerColor = SlateNavyDark,
                    labelColor = TextPrimaryDark,
                    iconColor = TextSecondaryDark
                ),
                shape = RoundedCornerShape(20.dp),
                border = null,
                modifier = Modifier
                    .shadow(2.dp, RoundedCornerShape(20.dp))
                    .testTag("category_chip_all")
            )

            // Category Chips: Barberías, Reposterías, Bodegas, Cafeterías, Restaurantes, etc.
            BusinessCategory.values().forEach { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectCategory(category) },
                    label = {
                        Text(
                            text = category.displayName,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = getCategoryIcon(category),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = ObsidianDark,
                        selectedLeadingIconColor = ObsidianDark,
                        containerColor = SlateNavyDark,
                        labelColor = TextPrimaryDark,
                        iconColor = TextSecondaryDark
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = null,
                    modifier = Modifier
                        .shadow(2.dp, RoundedCornerShape(20.dp))
                        .testTag("category_chip_${category.name.lowercase()}")
                )
            }

            // Quick "Abierto Ahora" toggle chip
            FilterChip(
                selected = onlyOpen,
                onClick = onToggleOnlyOpen,
                label = { Text("Abierto Ahora", fontSize = 12.sp) },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(if (onlyOpen) EmeraldTrust else AmberPending, CircleShape)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EmeraldContainer,
                    selectedLabelColor = OnEmeraldContainer,
                    selectedLeadingIconColor = EmeraldTrust,
                    containerColor = SlateNavyDark,
                    labelColor = TextPrimaryDark
                ),
                shape = RoundedCornerShape(20.dp),
                border = null,
                modifier = Modifier.testTag("chip_only_open")
            )
        }
    }
}
