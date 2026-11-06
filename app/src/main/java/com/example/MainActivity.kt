package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.splash.SplashScreen
import com.example.model.*
import com.example.ui.admin.AdminPanelScreen
import com.example.ui.business.BusinessDetailScreen
import com.example.ui.checkout.CheckoutSheet
import com.example.ui.components.UrbeBottomNavigationBar
import com.example.ui.components.UrbeTopBar
import com.example.ui.jobs.YouthJobsScreen
import com.example.ui.map.MapDiscoveryScreen
import com.example.ui.merchant.MerchantDashboardScreen
import com.example.ui.notifications.NotificationsScreen
import com.example.ui.orders.OrdersTrackingScreen
import com.example.ui.profile.UserProfileScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianDark
import com.example.viewmodel.AppScreen
import com.example.viewmodel.UrbeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                UrbeApp()
            }
        }
    }
}

@Composable
fun UrbeApp(
    viewModel: UrbeViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userRole by viewModel.userRoleMode.collectAsStateWithLifecycle()
    val unreadNotifsCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val cartSubtotal by viewModel.cartSubtotal.collectAsStateWithLifecycle()

    var showSplashScreen by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        viewModel.updateUserCoordinates(loc.latitude, loc.longitude)
                    }
                }
            } catch (_: SecurityException) {}
        }
    }

    LaunchedEffect(Unit) {
        val fineCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fineCheck != PackageManager.PERMISSION_GRANTED && coarseCheck != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        viewModel.updateUserCoordinates(loc.latitude, loc.longitude)
                    }
                }
            } catch (_: SecurityException) {}
        }
    }

    // Map & Filter states
    val publicBusinesses by viewModel.publicFilteredBusinesses.collectAsStateWithLifecycle()
    val allBusinesses by viewModel.allBusinesses.collectAsStateWithLifecycle()
    val selectedBusiness by viewModel.selectedBusiness.collectAsStateWithLifecycle()
    val activeRouteBusiness by viewModel.activeRouteBusiness.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val onlyOpen by viewModel.filterOnlyOpen.collectAsStateWithLifecycle()
    val onlyPromos by viewModel.filterOnlyPromos.collectAsStateWithLifecycle()
    val minRating by viewModel.filterMinRating.collectAsStateWithLifecycle()
    val maxDistanceKm by viewModel.filterMaxDistanceKm.collectAsStateWithLifecycle()
    val showFilterDialog by viewModel.showFilterDialog.collectAsStateWithLifecycle()

    // World & Creation states
    val currentMapCenter by viewModel.currentMapCenter.collectAsStateWithLifecycle()
    val selectedCity by viewModel.currentSelectedCity.collectAsStateWithLifecycle()
    val mapExploreMode by viewModel.mapExploreMode.collectAsStateWithLifecycle()
    val showCreateBusinessModal by viewModel.showCreateBusinessModal.collectAsStateWithLifecycle()
    val pinnedCreationCoords by viewModel.pinnedCreationCoordinates.collectAsStateWithLifecycle()

    // Commerce & Jobs states
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val jobOffers by viewModel.jobOffers.collectAsStateWithLifecycle()
    val jobApplications by viewModel.jobApplications.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val showCheckoutSheet by viewModel.showCheckoutSheet.collectAsStateWithLifecycle()
    val selectedJobForApply by viewModel.selectedJobForApply.collectAsStateWithLifecycle()
    val inspectingBusiness by viewModel.inspectingBusinessForAdmin.collectAsStateWithLifecycle()
    val showAddBusinessDialog by viewModel.showAddBusinessDialog.collectAsStateWithLifecycle()

    // Back handling
    BackHandler(enabled = currentScreen != AppScreen.MAP_DISCOVERY) {
        viewModel.navigateBack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = ObsidianDark,
            topBar = {
            // Do not show common top bar on Map Discovery (Map has its own integrated sleek search bar)
            if (currentScreen != AppScreen.MAP_DISCOVERY && currentScreen != AppScreen.BUSINESS_DETAIL) {
                UrbeTopBar(
                    title = when (currentScreen) {
                        AppScreen.YOUTH_JOBS -> "Bolsa de Empleo Joven"
                        AppScreen.ORDERS_TRACKING -> "Mis Pedidos & Tracking"
                        AppScreen.NOTIFICATIONS -> "Centro de Notificaciones"
                        AppScreen.ADMIN_PANEL -> "Panel de Control Admin"
                        AppScreen.MERCHANT_PANEL -> "Panel del Negocio"
                        AppScreen.USER_PROFILE -> "Mi Perfil & Roles"
                        else -> "Urbe"
                    },
                    subtitle = when (currentScreen) {
                        AppScreen.YOUTH_JOBS -> "Oportunidades sin experiencia"
                        AppScreen.ADMIN_PANEL -> "Verificación & Auditoría"
                        AppScreen.MERCHANT_PANEL -> "Ventas y pedidos en vivo"
                        else -> null
                    },
                    currentRole = userRole,
                    unreadNotifCount = unreadNotifsCount,
                    onRoleClick = { viewModel.navigateTo(AppScreen.USER_PROFILE) },
                    onNotificationClick = { viewModel.navigateTo(AppScreen.NOTIFICATIONS) },
                    onBackClick = if (currentScreen != AppScreen.MAP_DISCOVERY) {
                        { viewModel.navigateBack() }
                    } else null
                )
            }
        },
        bottomBar = {
            if (currentScreen != AppScreen.BUSINESS_DETAIL) {
                UrbeBottomNavigationBar(
                    currentScreen = currentScreen,
                    currentRole = userRole,
                    unreadNotifCount = unreadNotifsCount,
                    cartItemCount = cartItems.sumOf { it.quantity },
                    onNavigate = { destination -> viewModel.navigateTo(destination) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.MAP_DISCOVERY -> {
                    MapDiscoveryScreen(
                        businesses = publicBusinesses,
                        poiGuides = viewModel.mapPoiGuides,
                        userLocation = viewModel.currentUserLocation,
                        currentMapCenter = currentMapCenter,
                        worldCities = viewModel.worldCities,
                        selectedCity = selectedCity,
                        mapExploreMode = mapExploreMode,
                        selectedBusiness = selectedBusiness,
                        activeRouteBusiness = activeRouteBusiness,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        onlyOpen = onlyOpen,
                        onlyPromos = onlyPromos,
                        minRating = minRating,
                        maxDistanceKm = maxDistanceKm,
                        showFilterDialog = showFilterDialog,
                        showCreateBusinessModal = showCreateBusinessModal,
                        pinnedCreationCoords = pinnedCreationCoords,
                        currentRole = userRole,
                        onSwitchToAdminRole = { viewModel.setUserRoleMode(UserRoleMode.ADMIN) },
                        onQueryChange = { viewModel.setSearchQuery(it) },
                        onSelectCategory = { viewModel.setSelectedCategory(it) },
                        onToggleOnlyOpen = { viewModel.toggleFilterOnlyOpen() },
                        onToggleOnlyPromos = { viewModel.toggleFilterOnlyPromos() },
                        onMinRatingChange = { viewModel.setFilterMinRating(it) },
                        onMaxDistanceChange = { viewModel.setFilterMaxDistanceKm(it) },
                        onOpenFilterDialog = { viewModel.setShowFilterDialog(true) },
                        onCloseFilterDialog = { viewModel.setShowFilterDialog(false) },
                        onResetFilters = {
                            viewModel.resetFilters()
                            viewModel.setShowFilterDialog(false)
                        },
                        onSelectBusiness = { viewModel.selectBusiness(it) },
                        onClosePeekCard = { viewModel.selectBusiness(null) },
                        onOpenBusinessDetail = { viewModel.openBusinessDetail(it) },
                        onToggleRouteToBusiness = { viewModel.setRouteToBusiness(it) },
                        onClearRoute = { viewModel.clearRoute() },
                        onSelectWorldCity = { viewModel.selectWorldCity(it) },
                        onRecenterUserLocation = {
                            // Check permission and update coordinates if available
                            try {
                                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                                    if (loc != null) {
                                        viewModel.updateUserCoordinates(loc.latitude, loc.longitude)
                                    }
                                }
                            } catch (_: SecurityException) {}
                            viewModel.recenterOnUserLocation()
                        },
                        onOpenCreateBusiness = { lat, lng -> viewModel.openCreateBusinessModal(lat, lng) },
                        onCloseCreateBusiness = { viewModel.closeCreateBusinessModal() },
                        onSaveAndPublishBusiness = { viewModel.saveAndPublishNewBusiness(it) },
                        calculateDistance = { lat1, lng1, lat2, lng2 ->
                            viewModel.calculateDistanceKm(lat1, lng1, lat2, lng2)
                        }
                    )
                }

                AppScreen.BUSINESS_DETAIL -> {
                    val biz = selectedBusiness
                    if (biz != null) {
                        BusinessDetailScreen(
                            business = biz,
                            cartItems = cartItems,
                            cartSubtotal = cartSubtotal,
                            currentRole = userRole,
                            onUpdateBusiness = { updated -> viewModel.updateBusiness(updated) },
                            onAddToCart = { item, bId -> viewModel.addToCart(item, bId) },
                            onRemoveFromCart = { itemId -> viewModel.removeFromCart(itemId) },
                            onOpenCheckout = { viewModel.setShowCheckoutSheet(true) },
                            onBack = { viewModel.navigateBack() }
                        )
                    } else {
                        viewModel.navigateBack()
                    }
                }

                AppScreen.YOUTH_JOBS -> {
                    YouthJobsScreen(
                        jobOffers = jobOffers,
                        jobApplications = jobApplications,
                        selectedJobForApply = selectedJobForApply,
                        candidateName = viewModel.candidateName,
                        candidateAge = viewModel.candidateAge,
                        candidatePhone = viewModel.candidatePhone,
                        candidateBio = viewModel.candidateBio,
                        candidateAvailability = viewModel.candidateAvailability,
                        candidateSkills = viewModel.candidateSkills,
                        onSelectJobForApply = { viewModel.setSelectedJobForApply(it) },
                        onSubmitApplication = { viewModel.submitYouthJobApplication(it) }
                    )
                }

                AppScreen.ORDERS_TRACKING -> {
                    OrdersTrackingScreen(
                        orders = orders,
                        onAdvanceOrderStatus = { viewModel.repository.advanceOrderStatus(it) },
                        onNavigateToMap = { viewModel.navigateTo(AppScreen.MAP_DISCOVERY) }
                    )
                }

                AppScreen.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onMarkAsRead = { viewModel.markNotificationAsRead(it) },
                        onMarkAllAsRead = { viewModel.markAllNotificationsAsRead() }
                    )
                }

                AppScreen.ADMIN_PANEL -> {
                    AdminPanelScreen(
                        businesses = allBusinesses,
                        inspectingBusiness = inspectingBusiness,
                        showAddBusinessDialog = showAddBusinessDialog,
                        onInspectBusiness = { viewModel.setInspectingBusinessForAdmin(it) },
                        onApproveVerification = { bId, score, verdict ->
                            viewModel.approveBusinessVerification(bId, score, verdict)
                        },
                        onRejectVerification = { bId, reason ->
                            viewModel.rejectBusinessVerification(bId, reason)
                        },
                        onToggleVisibility = { viewModel.toggleBusinessVisibility(it) },
                        onToggleOpenClose = { viewModel.toggleBusinessOpenClose(it) },
                        onOpenAddBusinessDialog = { viewModel.setShowAddBusinessDialog(true) },
                        onCloseAddBusinessDialog = { viewModel.setShowAddBusinessDialog(false) },
                        onAddNewBusiness = { viewModel.addNewBusinessByAdmin(it) }
                    )
                }

                AppScreen.MERCHANT_PANEL -> {
                    MerchantDashboardScreen(
                        businesses = allBusinesses.filter { it.isEligibleForPublicDisplay },
                        orders = orders,
                        jobOffers = jobOffers,
                        onToggleOpenClose = { viewModel.toggleBusinessOpenClose(it) },
                        onToggleItemAvailability = { bId, itemId ->
                            viewModel.repository.toggleMenuItemAvailability(bId, itemId)
                        },
                        onAddMenuItem = { bId, item ->
                            viewModel.repository.addMenuItem(bId, item)
                        },
                        onPostJobOffer = { viewModel.postJobOffer(it) }
                    )
                }

                AppScreen.USER_PROFILE -> {
                    UserProfileScreen(
                        currentRole = userRole,
                        candidateName = viewModel.candidateName,
                        candidateAge = viewModel.candidateAge,
                        candidatePhone = viewModel.candidatePhone,
                        candidateBio = viewModel.candidateBio,
                        candidateAvailability = viewModel.candidateAvailability,
                        candidateSkills = viewModel.candidateSkills,
                        onRoleChange = { newRole ->
                            viewModel.setUserRoleMode(newRole)
                            when (newRole) {
                                UserRoleMode.ADMIN -> viewModel.navigateTo(AppScreen.ADMIN_PANEL)
                                UserRoleMode.MERCHANT -> viewModel.navigateTo(AppScreen.MERCHANT_PANEL)
                                UserRoleMode.EXPLORER -> viewModel.navigateTo(AppScreen.MAP_DISCOVERY)
                            }
                        }
                    )
                }
            }

            // Global Checkout Sheet
            if (showCheckoutSheet) {
                CheckoutSheet(
                    cartItems = cartItems,
                    subtotal = cartSubtotal,
                    onAddToCart = { item, bId -> viewModel.addToCart(item, bId) },
                    onRemoveFromCart = { itemId -> viewModel.removeFromCart(itemId) },
                    onProcessOrder = { method, address, notes ->
                        viewModel.processOrder(method, address, notes)
                    },
                    onDismiss = { viewModel.setShowCheckoutSheet(false) }
                )
            }
        }
    }

    // Luxury animated Splash Screen with black background and white branding
    AnimatedVisibility(
        visible = showSplashScreen,
        enter = fadeIn(),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        SplashScreen(onSplashFinished = { showSplashScreen = false })
    }
}
