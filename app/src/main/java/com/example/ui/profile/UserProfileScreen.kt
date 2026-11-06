package com.example.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.UserRoleMode
import com.example.ui.theme.*

@Composable
fun UserProfileScreen(
    currentRole: UserRoleMode,
    candidateName: String,
    candidateAge: Int,
    candidatePhone: String,
    candidateBio: String,
    candidateAvailability: String,
    candidateSkills: List<String>,
    onRoleChange: (UserRoleMode) -> Unit
) {
    Scaffold(
        containerColor = ObsidianDark
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Profile Card Header
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SlateNavyDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(CircleShape)
                                .background(GoldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = candidateName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                        Text(
                            text = "$candidateAge años • $candidatePhone",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ROLE SWITCHER: Explorador vs Merchant vs Admin
                Text(
                    text = "Seleccionar Rol de Acceso",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                )
                Text(
                    text = "Cambia de rol para probar las funciones de cliente, propietario o admin",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                )

                Spacer(modifier = Modifier.height(10.dp))

                UserRoleMode.values().forEach { role ->
                    val isSelected = currentRole == role
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
                            .clickable { onRoleChange(role) }
                            .testTag("switch_role_${role.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (role) {
                                            UserRoleMode.ADMIN -> GoldContainer
                                            UserRoleMode.MERCHANT -> EmeraldContainer
                                            UserRoleMode.EXPLORER -> SurfaceElevatedDark
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (role) {
                                        UserRoleMode.ADMIN -> Icons.Default.AdminPanelSettings
                                        UserRoleMode.MERCHANT -> Icons.Default.Storefront
                                        UserRoleMode.EXPLORER -> Icons.Default.Explore
                                    },
                                    contentDescription = null,
                                    tint = when (role) {
                                        UserRoleMode.ADMIN -> GoldPrimary
                                        UserRoleMode.MERCHANT -> EmeraldTrust
                                        UserRoleMode.EXPLORER -> TextPrimaryDark
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = role.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                )
                                Text(
                                    text = role.subtitle,
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                                )
                            }

                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // CV & Youth Employment Profile Section
                Text(
                    text = "Mi Perfil Laboral Juvenil",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                )
                Text(
                    text = "Datos vinculados a tus postulaciones de empleo joven",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SlateNavyDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Presentación personal:",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                        )
                        Text(
                            text = candidateBio,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, lineHeight = 16.sp)
                        )

                        Divider(color = SurfaceElevatedDark, modifier = Modifier.padding(vertical = 4.dp))

                        Text(
                            text = "Disponibilidad Horaria:",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                        )
                        Text(
                            text = candidateAvailability,
                            style = MaterialTheme.typography.bodySmall.copy(color = EmeraldTrust, fontWeight = FontWeight.SemiBold)
                        )

                        Divider(color = SurfaceElevatedDark, modifier = Modifier.padding(vertical = 4.dp))

                        Text(
                            text = "Habilidades destacadas:",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            candidateSkills.forEach { skill ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SurfaceCardDark
                                ) {
                                    Text(
                                        text = skill,
                                        style = MaterialTheme.typography.labelSmall.copy(color = GoldLight, fontSize = 10.sp),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
