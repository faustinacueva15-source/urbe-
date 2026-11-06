package com.example.model

enum class JobApplicationStatus(val label: String, val colorCode: Long) {
    POSTULADO("Postulación Enviada", 0xFF3B82F6),
    EN_REVISION("En Revisión por Negocio", 0xFFF59E0B),
    ENTREVISTA("Entrevista Agendada", 0xFF10B981),
    ACEPTADO("¡Contratado!", 0xFFD4AF37),
    NO_CONTINUA("No Seleccionado", 0xFF64748B)
}

data class JobOffer(
    val id: String,
    val businessId: String,
    val businessName: String,
    val businessCategory: BusinessCategory,
    val title: String,
    val description: String,
    val isYouthNoExperience: Boolean = true, // Focus on youth without experience
    val employmentType: String = "Medio Tiempo (Compatible con Estudios)", // or "Tiempo Completo", "Fines de semana"
    val salaryMonthly: String,
    val workSchedule: String,
    val address: String,
    val openingsCount: Int = 2,
    val requirements: List<String>,
    val benefits: List<String>,
    val postedDate: String = "Reciente"
)

data class JobApplication(
    val id: String,
    val jobOfferId: String,
    val jobTitle: String,
    val businessName: String,
    val candidateName: String,
    val candidateAge: Int,
    val candidatePhone: String,
    val candidateBio: String,
    val availability: String, // "Mañanas", "Tardes", "Fines de semana"
    val skills: List<String>, // e.g. "Atención al cliente", "Puntualidad", "Manejo de redes", "Trabajo en equipo"
    val status: JobApplicationStatus = JobApplicationStatus.POSTULADO,
    val appliedDate: String,
    val businessNotes: String = ""
)
