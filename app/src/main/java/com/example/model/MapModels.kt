package com.example.model

data class MapPoint(
    val lat: Double,
    val lng: Double,
    val name: String = ""
)

data class MapPoiGuide(
    val id: String,
    val title: String,
    val typeName: String, // "Vía Principal", "Parque Metropolitano", "Paseo Comercial", "Monumento Histórico"
    val description: String,
    val lat: Double,
    val lng: Double,
    val iconKey: String = "place"
)

data class WorldCity(
    val id: String,
    val name: String,
    val country: String,
    val flagEmoji: String,
    val lat: Double,
    val lng: Double,
    val description: String,
    val isUserLocation: Boolean = false
)

enum class MapExploreMode(val label: String, val iconName: String) {
    LOCAL_NEIGHBORHOOD("Mi Zona y Comercios", "my_location"),
    GLOBAL_WORLD("Mundo y Ciudades", "public")
}


enum class NotificationType(val title: String, val badgeColor: Long) {
    OFERTA_EXCLUSIVA("Oferta Exclusiva", 0xFFD4AF37),
    ESTADO_NEGOCIO("Actualización de Negocio", 0xFF10B981),
    PEDIDO_ACTUALIZACION("Estado de Pedido", 0xFF3B82F6),
    EMPLEO_JOVEN("Oportunidad Laboral", 0xFF8B5CF6),
    SISTEMA_ADMIN("Aviso del Sistema", 0xFF64748B)
}

data class AppNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val relatedBusinessId: String? = null
)

enum class UserRoleMode(val title: String, val subtitle: String) {
    EXPLORER("Explorador / Cliente", "Descubre negocios y pide a domicilio"),
    MERCHANT("Dueño de Negocio", "Gestiona tus ventas, pedidos y menú"),
    ADMIN("Administrador Urbe", "Valida documentos y audita la plataforma")
}
