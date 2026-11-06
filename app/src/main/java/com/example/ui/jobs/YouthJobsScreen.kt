package com.example.ui.jobs

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
import androidx.compose.ui.window.Dialog
import com.example.model.JobApplication
import com.example.model.JobApplicationStatus
import com.example.model.JobOffer
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.*

@Composable
fun YouthJobsScreen(
    jobOffers: List<JobOffer>,
    jobApplications: List<JobApplication>,
    selectedJobForApply: JobOffer?,
    candidateName: String,
    candidateAge: Int,
    candidatePhone: String,
    candidateBio: String,
    candidateAvailability: String,
    candidateSkills: List<String>,
    onSelectJobForApply: (JobOffer?) -> Unit,
    onSubmitApplication: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Ofertas, 1: Mis Postulaciones

    Scaffold(
        containerColor = ObsidianDark
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Hero Banner: Empleo Joven
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SlateNavyDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Work,
                                    contentDescription = null,
                                    tint = ObsidianDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Bolsa de Empleo Joven",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GoldLight
                                    )
                                )
                                Text(
                                    text = "Oportunidades de primer empleo • Sin experiencia previa",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Conectamos jóvenes motivados con comercios locales verificados. Capacitación desde cero, horarios flexibles compatibles con estudios y postulación directa desde tu perfil.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimaryDark,
                                lineHeight = 17.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Selector: Ofertas vs Mis Postulaciones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { selectedTab = 0 },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == 0) GoldPrimary else SurfaceCardDark,
                            contentColor = if (selectedTab == 0) ObsidianDark else TextPrimaryDark
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_job_offers")
                    ) {
                        Text("Ofertas Laborales (${jobOffers.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { selectedTab = 1 },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == 1) GoldPrimary else SurfaceCardDark,
                            contentColor = if (selectedTab == 1) ObsidianDark else TextPrimaryDark
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_my_applications")
                    ) {
                        Text("Mis Postulaciones (${jobApplications.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Tab 0: Job Offers List
            if (selectedTab == 0) {
                items(jobOffers, key = { it.id }) { job ->
                    val isAlreadyApplied = jobApplications.any { it.jobOfferId == job.id }

                    YouthJobOfferCard(
                        job = job,
                        isAlreadyApplied = isAlreadyApplied,
                        onApplyClick = { onSelectJobForApply(job) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                // Tab 1: My Applications List
                if (jobApplications.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aún no te has postulado a ninguna vacante laboral.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondaryDark)
                            )
                        }
                    }
                } else {
                    items(jobApplications, key = { it.id }) { app ->
                        ApplicationStatusCard(application = app)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }

    // Modal Dialog: 1-Click Application from Personal Profile
    if (selectedJobForApply != null) {
        val job = selectedJobForApply
        Dialog(onDismissRequest = { onSelectJobForApply(null) }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SlateNavyDark,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("job_application_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Postularme en 1 Clic",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                            )
                            Text(
                                text = "Perfil de Postulante Juvenil",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                            )
                        }
                        IconButton(onClick = { onSelectJobForApply(null) }) {
                            Icon(Icons.Default.Close, null, tint = TextSecondaryDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Job Title Target
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceCardDark,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Puesto: ${job.title}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                            )
                            Text(
                                text = "Empresa: ${job.businessName} (${job.salaryMonthly})",
                                style = MaterialTheme.typography.bodySmall.copy(color = GoldLight)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Datos de tu Perfil que se enviarán:",
                        style = MaterialTheme.typography.labelMedium.copy(color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceCardDark,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Nombre:", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark))
                                Text("$candidateName ($candidateAge años)", style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontWeight = FontWeight.Bold))
                            }
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Teléfono de Contacto:", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark))
                                Text(candidatePhone, style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark))
                            }
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Disponibilidad:", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark))
                                Text(candidateAvailability, style = MaterialTheme.typography.bodySmall.copy(color = EmeraldTrust, fontWeight = FontWeight.SemiBold))
                            }
                            Divider(color = SurfaceElevatedDark)
                            Text(
                                text = "Habilidades declaradas:",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                candidateSkills.take(3).forEach { skill ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SlateNavyDark
                                    ) {
                                        Text(
                                            text = skill,
                                            style = MaterialTheme.typography.labelSmall.copy(color = GoldLight, fontSize = 9.sp),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { onSubmitApplication(job.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = ObsidianDark
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("confirm_job_application_button")
                    ) {
                        Icon(Icons.Default.Send, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enviar Postulación Inmediata", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun YouthJobOfferCard(
    job: JobOffer,
    isAlreadyApplied: Boolean,
    onApplyClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SlateNavyDark,
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("job_card_${job.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Category, Youth Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(GoldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(job.businessCategory),
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = job.businessName,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldContainer
                ) {
                    Text(
                        text = "Sin Experiencia",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnEmeraldContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Job Title
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GoldLight,
                    fontSize = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Description
            Text(
                text = job.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondaryDark,
                    lineHeight = 17.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Badges row: Salary, Type, Vacancies
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SurfaceCardDark
                ) {
                    Text(
                        text = job.salaryMonthly,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SurfaceCardDark
                ) {
                    Text(
                        text = job.employmentType,
                        style = MaterialTheme.typography.labelSmall.copy(color = TextPrimaryDark),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Requirements bullet points
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                job.requirements.take(2).forEach { req ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = EmeraldTrust, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(req, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark, fontSize = 11.sp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Apply Button
            if (isAlreadyApplied) {
                OutlinedButton(
                    onClick = { },
                    enabled = false,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldTrust),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ya te has postulado a este empleo", fontSize = 12.sp)
                }
            } else {
                Button(
                    onClick = onApplyClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = ObsidianDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("apply_job_button_${job.id}")
                ) {
                    Icon(Icons.Default.Send, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Postularme en 1 Clic con mi Perfil", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ApplicationStatusCard(application: JobApplication) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SlateNavyDark,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = application.jobTitle,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )
                    Text(
                        text = "${application.businessName} • Postulado el ${application.appliedDate}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(application.status.colorCode).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = application.status.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(application.status.colorCode),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SurfaceCardDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = application.businessNotes,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark, fontSize = 11.sp)
                    )
                }
            }
        }
    }
}
