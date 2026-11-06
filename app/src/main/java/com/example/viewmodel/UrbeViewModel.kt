package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.UrbeRepository
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.*

enum class AppScreen {
    MAP_DISCOVERY,
    BUSINESS_DETAIL,
    YOUTH_JOBS,
    ORDERS_TRACKING,
    NOTIFICATIONS,
    ADMIN_PANEL,
    MERCHANT_PANEL,
    USER_PROFILE
}

data class FilterCriteria(
    val query: String = "",
    val selectedCategory: BusinessCategory? = null,
    val onlyOpen: Boolean = false,
    val onlyPromos: Boolean = false,
    val minRating: Double = 0.0,
    val maxDistanceKm: Float = 15.0f
)

class UrbeViewModel(
    val repository: UrbeRepository = UrbeRepository()
) : ViewModel() {

    // Current Navigation Screen
    private val _currentScreen = MutableStateFlow(AppScreen.MAP_DISCOVERY)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Screen Backstack
    private val screenStack = mutableListOf(AppScreen.MAP_DISCOVERY)

    // Unified Filter State
    private val _filterCriteria = MutableStateFlow(FilterCriteria())
    val filterCriteria: StateFlow<FilterCriteria> = _filterCriteria.asStateFlow()

    // Expose individual getters for UI convenience
    val searchQuery: StateFlow<String> = _filterCriteria.map { it.query }.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val selectedCategory: StateFlow<BusinessCategory?> = _filterCriteria.map { it.selectedCategory }.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val filterOnlyOpen: StateFlow<Boolean> = _filterCriteria.map { it.onlyOpen }.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val filterOnlyPromos: StateFlow<Boolean> = _filterCriteria.map { it.onlyPromos }.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val filterMinRating: StateFlow<Double> = _filterCriteria.map { it.minRating }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)
    val filterMaxDistanceKm: StateFlow<Float> = _filterCriteria.map { it.maxDistanceKm }.stateIn(viewModelScope, SharingStarted.Eagerly, 15.0f)

    // Map selection & Interaction states
    private val _selectedBusiness = MutableStateFlow<Business?>(null)
    val selectedBusiness: StateFlow<Business?> = _selectedBusiness.asStateFlow()

    private val _activeRouteBusiness = MutableStateFlow<Business?>(null)
    val activeRouteBusiness: StateFlow<Business?> = _activeRouteBusiness.asStateFlow()

    // World & Regional Map Exploration States
    val worldCities = repository.worldCities
    private val _currentMapCenter = MutableStateFlow(repository.currentUserLocation)
    val currentMapCenter: StateFlow<MapPoint> = _currentMapCenter.asStateFlow()

    private val _currentSelectedCity = MutableStateFlow(repository.worldCities.first())
    val currentSelectedCity: StateFlow<WorldCity> = _currentSelectedCity.asStateFlow()

    private val _mapExploreMode = MutableStateFlow(MapExploreMode.LOCAL_NEIGHBORHOOD)
    val mapExploreMode: StateFlow<MapExploreMode> = _mapExploreMode.asStateFlow()

    // Create Business Modal State (Direct creation by user/admin)
    private val _showCreateBusinessModal = MutableStateFlow(false)
    val showCreateBusinessModal: StateFlow<Boolean> = _showCreateBusinessModal.asStateFlow()

    private val _pinnedCreationCoordinates = MutableStateFlow<Pair<Double, Double>?>(null)
    val pinnedCreationCoordinates: StateFlow<Pair<Double, Double>?> = _pinnedCreationCoordinates.asStateFlow()

    // UI Dialog & Sheet states
    private val _showFilterDialog = MutableStateFlow(false)
    val showFilterDialog: StateFlow<Boolean> = _showFilterDialog.asStateFlow()

    private val _showCheckoutSheet = MutableStateFlow(false)
    val showCheckoutSheet: StateFlow<Boolean> = _showCheckoutSheet.asStateFlow()

    private val _selectedJobForApply = MutableStateFlow<JobOffer?>(null)
    val selectedJobForApply: StateFlow<JobOffer?> = _selectedJobForApply.asStateFlow()

    private val _inspectingBusinessForAdmin = MutableStateFlow<Business?>(null)
    val inspectingBusinessForAdmin: StateFlow<Business?> = _inspectingBusinessForAdmin.asStateFlow()

    private val _showAddBusinessDialog = MutableStateFlow(false)
    val showAddBusinessDialog: StateFlow<Boolean> = _showAddBusinessDialog.asStateFlow()

    private val _showPostJobDialog = MutableStateFlow(false)
    val showPostJobDialog: StateFlow<Boolean> = _showPostJobDialog.asStateFlow()

    // User Profile Data for Youth Job Applications
    var candidateName = "Julián Morales"
    var candidateAge = 21
    var candidatePhone = "+51 977 123 456"
    var candidateBio = "Joven con entusiasmo y alta responsabilidad. Busco mi primera experiencia laboral formal, aprendo rápido y tengo total disponibilidad horaria."
    var candidateAvailability = "Medio Tiempo / Fines de semana"
    var candidateSkills = listOf("Puntualidad", "Atención al Cliente", "Trabajo en Equipo", "Manejo de Computadora / Celular")

    // Base flows from repository
    val allBusinesses = repository.businesses
    val jobOffers = repository.jobOffers
    val jobApplications = repository.jobApplications
    val orders = repository.orders
    val notifications = repository.notifications
    val cartItems = repository.cartItems
    val cartBusinessId = repository.cartBusinessId
    val userRoleMode = repository.userRoleMode
    val currentUserLocation = repository.currentUserLocation
    val mapPoiGuides = repository.mapPoiGuides

    // FILTERED BUSINESSES for public display
    val publicFilteredBusinesses: StateFlow<List<Business>> = combine(
        allBusinesses,
        _filterCriteria,
        _currentMapCenter
    ) { bizList: List<Business>, filters: FilterCriteria, mapCenter: MapPoint ->
        bizList.filter { biz ->
            // Must be eligible for public display
            biz.isEligibleForPublicDisplay &&
            // Text Search
            (filters.query.isBlank() || biz.name.contains(filters.query, ignoreCase = true) ||
             biz.category.displayName.contains(filters.query, ignoreCase = true) ||
             biz.description.contains(filters.query, ignoreCase = true) ||
             biz.menuItems.any { it.name.contains(filters.query, ignoreCase = true) }) &&
            // Category filter
            (filters.selectedCategory == null || biz.category == filters.selectedCategory) &&
            // Open now filter
            (!filters.onlyOpen || biz.isOpen) &&
            // Promotions filter
            (!filters.onlyPromos || biz.promotions.isNotEmpty()) &&
            // Rating filter
            (biz.rating >= filters.minRating) &&
            // Distance filter relative to the current active map focus
            (calculateDistanceKm(mapCenter.lat, mapCenter.lng, biz.latitude, biz.longitude) <= filters.maxDistanceKm)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart Total
    val cartSubtotal: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.lineTotal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val unreadNotificationsCount: StateFlow<Int> = notifications.map { notifs ->
        notifs.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Navigation functions
    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
            _currentScreen.value = screenStack.last()
            return true
        }
        return false
    }

    // World & Location navigation
    fun selectWorldCity(city: WorldCity) {
        _currentSelectedCity.value = city
        _currentMapCenter.value = MapPoint(city.lat, city.lng, city.name)
        _mapExploreMode.value = if (city.isUserLocation) MapExploreMode.LOCAL_NEIGHBORHOOD else MapExploreMode.GLOBAL_WORLD
        _selectedBusiness.value = null
    }

    fun recenterOnUserLocation() {
        val userLoc = repository.currentUserLocation
        _currentMapCenter.value = userLoc
        _currentSelectedCity.value = repository.worldCities.first { it.isUserLocation }
        _mapExploreMode.value = MapExploreMode.LOCAL_NEIGHBORHOOD
    }

    fun setMapExploreMode(mode: MapExploreMode) {
        _mapExploreMode.value = mode
    }

    // Creation of New Business
    fun openCreateBusinessModal(lat: Double? = null, lng: Double? = null) {
        val targetLat = lat ?: _currentMapCenter.value.lat
        val targetLng = lng ?: _currentMapCenter.value.lng
        _pinnedCreationCoordinates.value = Pair(targetLat, targetLng)
        _showCreateBusinessModal.value = true
    }

    fun closeCreateBusinessModal() {
        _showCreateBusinessModal.value = false
        _pinnedCreationCoordinates.value = null
    }

    fun saveAndPublishNewBusiness(business: Business) {
        repository.addNewBusinessByAdmin(business)
        _selectedBusiness.value = business
        _currentMapCenter.value = MapPoint(business.latitude, business.longitude, business.name)
        closeCreateBusinessModal()
    }

    // Filter controls
    fun setSearchQuery(query: String) {
        _filterCriteria.value = _filterCriteria.value.copy(query = query)
    }

    fun setSelectedCategory(category: BusinessCategory?) {
        val current = _filterCriteria.value.selectedCategory
        _filterCriteria.value = _filterCriteria.value.copy(
            selectedCategory = if (current == category) null else category
        )
    }

    fun toggleFilterOnlyOpen() {
        _filterCriteria.value = _filterCriteria.value.copy(
            onlyOpen = !_filterCriteria.value.onlyOpen
        )
    }

    fun toggleFilterOnlyPromos() {
        _filterCriteria.value = _filterCriteria.value.copy(
            onlyPromos = !_filterCriteria.value.onlyPromos
        )
    }

    fun setFilterMinRating(rating: Double) {
        _filterCriteria.value = _filterCriteria.value.copy(minRating = rating)
    }

    fun setFilterMaxDistanceKm(distanceKm: Float) {
        _filterCriteria.value = _filterCriteria.value.copy(maxDistanceKm = distanceKm)
    }

    fun resetFilters() {
        _filterCriteria.value = FilterCriteria()
    }

    fun setShowFilterDialog(show: Boolean) {
        _showFilterDialog.value = show
    }

    fun selectBusiness(business: Business?) {
        _selectedBusiness.value = business
    }

    fun openBusinessDetail(business: Business) {
        _selectedBusiness.value = business
        navigateTo(AppScreen.BUSINESS_DETAIL)
    }

    fun setRouteToBusiness(business: Business?) {
        _activeRouteBusiness.value = business
        if (business != null) {
            _selectedBusiness.value = business
        }
    }

    fun clearRoute() {
        _activeRouteBusiness.value = null
    }

    // Cart and Checkout
    fun addToCart(item: MenuItem, businessId: String) {
        repository.addToCart(item, businessId)
    }

    fun removeFromCart(itemId: String) {
        repository.removeFromCart(itemId)
    }

    fun setShowCheckoutSheet(show: Boolean) {
        _showCheckoutSheet.value = show
    }

    fun processOrder(paymentMethod: PaymentMethod, address: String, notes: String): Order? {
        val order = repository.placeOrder(paymentMethod, address, notes)
        if (order != null) {
            _showCheckoutSheet.value = false
            navigateTo(AppScreen.ORDERS_TRACKING)
        }
        return order
    }

    // Youth Jobs
    fun setSelectedJobForApply(job: JobOffer?) {
        _selectedJobForApply.value = job
    }

    fun submitYouthJobApplication(jobOfferId: String) {
        repository.applyForJob(
            jobOfferId = jobOfferId,
            applicantName = candidateName,
            applicantAge = candidateAge,
            applicantPhone = candidatePhone,
            applicantBio = candidateBio,
            availability = candidateAvailability,
            skills = candidateSkills
        )
        _selectedJobForApply.value = null
    }

    // Admin & Document Verification
    fun setInspectingBusinessForAdmin(business: Business?) {
        _inspectingBusinessForAdmin.value = business
    }

    fun approveBusinessVerification(businessId: String, score: Int, verdict: String) {
        repository.approveBusinessVerification(businessId, score, verdict)
        _inspectingBusinessForAdmin.value = repository.businesses.value.firstOrNull { it.id == businessId }
    }

    fun rejectBusinessVerification(businessId: String, reason: String) {
        repository.rejectBusinessVerification(businessId, reason)
        _inspectingBusinessForAdmin.value = repository.businesses.value.firstOrNull { it.id == businessId }
    }

    fun toggleBusinessVisibility(businessId: String) {
        repository.toggleBusinessVisibility(businessId)
    }

    fun toggleBusinessOpenClose(businessId: String) {
        repository.toggleBusinessOpenClose(businessId)
    }

    fun setShowAddBusinessDialog(show: Boolean) {
        _showAddBusinessDialog.value = show
    }

    fun setShowPostJobDialog(show: Boolean) {
        _showPostJobDialog.value = show
    }

    fun addNewBusinessByAdmin(newBiz: Business) {
        repository.addNewBusinessByAdmin(newBiz)
        _showAddBusinessDialog.value = false
    }

    fun updateBusiness(updatedBiz: Business) {
        repository.updateBusiness(updatedBiz)
        if (_selectedBusiness.value?.id == updatedBiz.id) {
            _selectedBusiness.value = updatedBiz
        }
        if (_inspectingBusinessForAdmin.value?.id == updatedBiz.id) {
            _inspectingBusinessForAdmin.value = updatedBiz
        }
    }

    fun updateUserCoordinates(lat: Double, lng: Double) {
        repository.updateUserLocation(lat, lng)
        _currentMapCenter.value = repository.currentUserLocation
    }

    fun postJobOffer(jobOffer: JobOffer) {
        repository.postJobOffer(jobOffer)
        _showPostJobDialog.value = false
    }

    // Notifications
    fun markNotificationAsRead(id: String) {
        repository.markNotificationAsRead(id)
    }

    fun markAllNotificationsAsRead() {
        repository.markAllNotificationsAsRead()
    }

    // Role switcher
    fun setUserRoleMode(mode: UserRoleMode) {
        repository.setUserRoleMode(mode)
    }

    // Utility for distance calculation (Haversine formula approximation)
    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return 6371.0 * c // Earth radius in km
    }
}
