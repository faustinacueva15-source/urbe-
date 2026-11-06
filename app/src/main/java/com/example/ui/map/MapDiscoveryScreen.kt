package com.example.ui.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.business.CreateBusinessDialog
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateNavyDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.GoldLight

@Composable
fun MapDiscoveryScreen(
    businesses: List<Business>,
    poiGuides: List<MapPoiGuide>,
    userLocation: MapPoint,
    currentMapCenter: MapPoint,
    worldCities: List<WorldCity>,
    selectedCity: WorldCity,
    mapExploreMode: MapExploreMode,
    selectedBusiness: Business?,
    activeRouteBusiness: Business?,
    searchQuery: String,
    selectedCategory: BusinessCategory?,
    onlyOpen: Boolean,
    onlyPromos: Boolean,
    minRating: Double,
    maxDistanceKm: Float,
    showFilterDialog: Boolean,
    showCreateBusinessModal: Boolean,
    pinnedCreationCoords: Pair<Double, Double>?,
    currentRole: UserRoleMode = UserRoleMode.EXPLORER,
    onSwitchToAdminRole: () -> Unit = {},
    onQueryChange: (String) -> Unit,
    onSelectCategory: (BusinessCategory?) -> Unit,
    onToggleOnlyOpen: () -> Unit,
    onToggleOnlyPromos: () -> Unit,
    onMinRatingChange: (Double) -> Unit,
    onMaxDistanceChange: (Float) -> Unit,
    onOpenFilterDialog: () -> Unit,
    onCloseFilterDialog: () -> Unit,
    onResetFilters: () -> Unit,
    onSelectBusiness: (Business) -> Unit,
    onClosePeekCard: () -> Unit,
    onOpenBusinessDetail: (Business) -> Unit,
    onToggleRouteToBusiness: (Business) -> Unit,
    onClearRoute: () -> Unit,
    onSelectWorldCity: (WorldCity) -> Unit,
    onRecenterUserLocation: () -> Unit,
    onOpenCreateBusiness: (Double?, Double?) -> Unit,
    onCloseCreateBusiness: () -> Unit,
    onSaveAndPublishBusiness: (Business) -> Unit,
    calculateDistance: (Double, Double, Double, Double) -> Double
) {
    var showAdminOnlyAlert by remember { mutableStateOf(false) }

    fun handleRequestCreateBusiness(lat: Double?, lng: Double?) {
        if (currentRole == UserRoleMode.ADMIN) {
            onOpenCreateBusiness(lat, lng)
        } else {
            showAdminOnlyAlert = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Interactive 2D World/City Map Canvas with Reliefs, Street Names & City Exploration
        InteractiveMapCanvas(
            businesses = businesses,
            poiGuides = poiGuides,
            userLocation = userLocation,
            currentMapCenter = currentMapCenter,
            worldCities = worldCities,
            selectedCity = selectedCity,
            mapExploreMode = mapExploreMode,
            selectedBusiness = selectedBusiness,
            activeRouteBusiness = activeRouteBusiness,
            onSelectBusiness = onSelectBusiness,
            onClearRoute = onClearRoute,
            onSelectWorldCity = onSelectWorldCity,
            onRecenterUserLocation = onRecenterUserLocation,
            onOpenCreateBusinessAtLocation = { lat, lng -> handleRequestCreateBusiness(lat, lng) },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Search & Category Bar at Top
        MapSearchBar(
            query = searchQuery,
            onQueryChange = onQueryChange,
            selectedCategory = selectedCategory,
            onSelectCategory = onSelectCategory,
            onlyOpen = onlyOpen,
            onToggleOnlyOpen = onToggleOnlyOpen,
            minRating = minRating,
            maxDistanceKm = maxDistanceKm,
            onOpenFilterDialog = onOpenFilterDialog,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // 3. Floating Action Button: "+ Registrar Negocio" (Restricted to Admin Mode)
        ExtendedFloatingActionButton(
            onClick = { handleRequestCreateBusiness(null, null) },
            containerColor = GoldPrimary,
            contentColor = ObsidianDark,
            shape = RoundedCornerShape(16.dp),
            icon = { Icon(Icons.Default.AddBusiness, null) },
            text = {
                Text(
                    if (currentRole == UserRoleMode.ADMIN) "+ Registrar Negocio (Admin)" else "+ Registrar Negocio",
                    fontWeight = FontWeight.Bold
                )
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = if (selectedBusiness != null) 200.dp else 16.dp)
                .testTag("map_create_business_fab")
        )

        // Alert dialog when non-admin attempts to create a business
        if (showAdminOnlyAlert) {
            AlertDialog(
                onDismissRequest = { showAdminOnlyAlert = false },
                containerColor = SlateNavyDark,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Modo Administrador Exclusivo",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "Por control y verificación de documentos oficiales (RUC, licencias, fotos y catálogo verificado), la creación e inserción de nuevos negocios está reservada exclusivamente para el Administrador Central de Urbe.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondaryDark, lineHeight = 20.sp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "¿Deseas cambiar a Modo Administrador para registrar, fijar coordenadas y publicar este negocio ahora?",
                            style = MaterialTheme.typography.bodySmall.copy(color = GoldLight, fontWeight = FontWeight.SemiBold)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showAdminOnlyAlert = false
                            onSwitchToAdminRole()
                            onOpenCreateBusiness(null, null)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Activar Modo Admin", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAdminOnlyAlert = false }) {
                        Text("Cancelar", color = TextSecondaryDark)
                    }
                }
            )
        }

        // 4. Peek Card at Bottom when a business is tapped
        AnimatedVisibility(
            visible = selectedBusiness != null,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        ) {
            if (selectedBusiness != null) {
                val distance = calculateDistance(
                    userLocation.lat,
                    userLocation.lng,
                    selectedBusiness.latitude,
                    selectedBusiness.longitude
                )
                val isRouteActive = activeRouteBusiness?.id == selectedBusiness.id

                BusinessPeekCard(
                    business = selectedBusiness,
                    distanceKm = distance,
                    isRouteActive = isRouteActive,
                    onOpenDetail = { onOpenBusinessDetail(selectedBusiness) },
                    onToggleRoute = {
                        if (isRouteActive) onClearRoute() else onToggleRouteToBusiness(selectedBusiness)
                    },
                    onClose = onClosePeekCard
                )
            }
        }

        // 5. Custom Filter Modal Dialog
        if (showFilterDialog) {
            MapFilterDialog(
                onlyOpen = onlyOpen,
                onToggleOnlyOpen = onToggleOnlyOpen,
                onlyPromos = onlyPromos,
                onToggleOnlyPromos = onToggleOnlyPromos,
                minRating = minRating,
                onMinRatingChange = onMinRatingChange,
                maxDistanceKm = maxDistanceKm,
                onMaxDistanceChange = onMaxDistanceChange,
                onDismiss = onCloseFilterDialog,
                onReset = onResetFilters
            )
        }

        // 6. Direct Create Business Modal (With all specifications, saves and displays on map)
        if (showCreateBusinessModal) {
            val targetLat = pinnedCreationCoords?.first ?: currentMapCenter.lat
            val targetLng = pinnedCreationCoords?.second ?: currentMapCenter.lng
            CreateBusinessDialog(
                initialLat = targetLat,
                initialLng = targetLng,
                onDismiss = onCloseCreateBusiness,
                onSaveAndPublish = onSaveAndPublishBusiness
            )
        }
    }
}
