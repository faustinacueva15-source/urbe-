package com.example.model

enum class BusinessCategory(val displayName: String, val iconName: String) {
    BARBERIA("Barberías", "content_cut"),
    REPOSTERIA("Reposterías", "cake"),
    BODEGA("Bodegas", "storefront"),
    CAFETERIA("Cafeterías", "coffee"),
    RESTAURANTE("Restaurantes", "restaurant"),
    FARMACIA("Farmacias", "local_pharmacy"),
    GOURMET("Gourmet & Delicatessen", "wine_bar")
}

enum class VerificationStatus(val label: String, val isPublicVisible: Boolean) {
    PENDIENTE_REVISION("En Revisión de Admin", false),
    DOCUMENTOS_REQUERIDOS("Faltan Documentos", false),
    VERIFICADO_PUBLICADO("100% Verificado & Público", true),
    RECHAZADO("Rechazado", false),
    SUSPENDIDO("Temporalmente Suspendido", false)
}

enum class DocumentType(val title: String, val issuingAuthority: String) {
    RUC_TRIBUTARIO("RUC Activo y Habido (Opcional)", "Administración Tributaria / SUNAT"),
    LICENCIA_MUNICIPAL("Licencia de Funcionamiento (Opcional)", "Municipalidad Metropolitana"),
    CERTIFICADO_SANITARIO("Inspección Higiénico Sanitaria", "Autoridad de Salud / DIGESA"),
    DNI_REPRESENTANTE("Validación de Identidad de Titular", "Registro Nacional de Identificación"),
    DEFENSA_CIVIL("Certificado de Seguridad en Edificaciones", "Inspección Técnica ITSE / INDECI")
}

data class VerificationDocument(
    val id: String,
    val documentType: DocumentType,
    val documentNumber: String,
    val fileName: String,
    val uploadDate: String,
    val isApproved: Boolean,
    val verificationNotes: String = ""
)

data class MenuItem(
    val id: String,
    val businessId: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val isAvailable: Boolean = true,
    val originalPrice: Double? = null,
    val badge: String? = null,
    val photoUrl: String? = null,
    val imageType: String = "food"
)

data class Promotion(
    val id: String,
    val businessId: String,
    val title: String,
    val description: String,
    val discountPercent: Int,
    val promoCode: String,
    val expiresText: String,
    val isExclusive: Boolean = true
)

data class Business(
    val id: String,
    val name: String,
    val category: BusinessCategory,
    val description: String,
    val rating: Double,
    val reviewCount: Int,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String,
    val openingHours: String,
    val websiteUrl: String? = null,
    val photoUrls: List<String> = emptyList(),
    val coverPhotoUrl: String? = null,
    val isOpen: Boolean = true,
    val isVisibleInApp: Boolean = true, // Real-time toggle by Admin
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFICADO_PUBLICADO,
    val reputationScore: Int = 98, // 0 - 100
    val reputationVerdict: String = "Reputación Comercial Excelente",
    val verifiedByAdminDate: String = "01 Nov 2026",
    val documents: List<VerificationDocument> = emptyList(),
    val menuItems: List<MenuItem> = emptyList(),
    val promotions: List<Promotion> = emptyList(),
    val ownerName: String = "Propietario Verificado",
    val ownerEmail: String = "admin@urbe.com"
) {
    val isEligibleForPublicDisplay: Boolean
        get() = verificationStatus == VerificationStatus.VERIFICADO_PUBLICADO && isVisibleInApp
}
