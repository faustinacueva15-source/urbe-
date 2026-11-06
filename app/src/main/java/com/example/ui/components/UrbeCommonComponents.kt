package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BusinessCategory
import com.example.model.UserRoleMode
import com.example.model.VerificationStatus
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen

@Composable
fun UrbeTopBar(
    title: String,
    subtitle: String? = null,
    currentRole: UserRoleMode,
    unreadNotifCount: Int = 0,
    onRoleClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    Surface(
        color = SlateNavyDark,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("topbar_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = GoldPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GoldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Urbe Logo",
                            tint = GoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark,
                            letterSpacing = 0.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondaryDark
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Role Badge Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when (currentRole) {
                        UserRoleMode.ADMIN -> GoldContainer
                        UserRoleMode.MERCHANT -> EmeraldContainer
                        UserRoleMode.EXPLORER -> SurfaceElevatedDark
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onRoleClick() }
                        .testTag("role_badge_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (currentRole) {
                                UserRoleMode.ADMIN -> Icons.Default.AdminPanelSettings
                                UserRoleMode.MERCHANT -> Icons.Default.Storefront
                                UserRoleMode.EXPLORER -> Icons.Default.Explore
                            },
                            contentDescription = null,
                            tint = when (currentRole) {
                                UserRoleMode.ADMIN -> GoldPrimary
                                UserRoleMode.MERCHANT -> EmeraldTrust
                                UserRoleMode.EXPLORER -> TextSecondaryDark
                            },
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (currentRole) {
                                UserRoleMode.ADMIN -> "Admin"
                                UserRoleMode.MERCHANT -> "Negocio"
                                UserRoleMode.EXPLORER -> "Explorador"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = when (currentRole) {
                                    UserRoleMode.ADMIN -> GoldLight
                                    UserRoleMode.MERCHANT -> OnEmeraldContainer
                                    UserRoleMode.EXPLORER -> TextPrimaryDark
                                }
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Notifications Button
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("notifications_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotifCount > 0) {
                                Badge(
                                    containerColor = RubyAlert,
                                    contentColor = Color.White
                                ) {
                                    Text(text = if (unreadNotifCount > 9) "9+" else unreadNotifCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = TextPrimaryDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UrbeBottomNavigationBar(
    currentScreen: AppScreen,
    currentRole: UserRoleMode,
    unreadNotifCount: Int,
    cartItemCount: Int,
    onNavigate: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = SlateNavyDark,
        contentColor = TextPrimaryDark,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("urbe_bottom_navigation")
    ) {
        // Tab 1: Mapa / Explorar
        NavigationBarItem(
            selected = currentScreen == AppScreen.MAP_DISCOVERY || currentScreen == AppScreen.BUSINESS_DETAIL,
            onClick = { onNavigate(AppScreen.MAP_DISCOVERY) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.MAP_DISCOVERY) Icons.Filled.Map else Icons.Outlined.Map,
                    contentDescription = "Mapa"
                )
            },
            label = { Text("Mapa") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ObsidianDark,
                selectedTextColor = GoldPrimary,
                indicatorColor = GoldPrimary,
                unselectedIconColor = TextSecondaryDark,
                unselectedTextColor = TextSecondaryDark
            ),
            modifier = Modifier.testTag("nav_item_map")
        )

        // Tab 2: Empleo Joven
        NavigationBarItem(
            selected = currentScreen == AppScreen.YOUTH_JOBS,
            onClick = { onNavigate(AppScreen.YOUTH_JOBS) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.YOUTH_JOBS) Icons.Filled.Work else Icons.Outlined.WorkOutline,
                    contentDescription = "Empleo Joven"
                )
            },
            label = { Text("Empleo") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ObsidianDark,
                selectedTextColor = GoldPrimary,
                indicatorColor = GoldPrimary,
                unselectedIconColor = TextSecondaryDark,
                unselectedTextColor = TextSecondaryDark
            ),
            modifier = Modifier.testTag("nav_item_jobs")
        )

        // Tab 3: Pedidos
        NavigationBarItem(
            selected = currentScreen == AppScreen.ORDERS_TRACKING,
            onClick = { onNavigate(AppScreen.ORDERS_TRACKING) },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(containerColor = GoldPrimary, contentColor = ObsidianDark) {
                                Text("$cartItemCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentScreen == AppScreen.ORDERS_TRACKING) Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
                        contentDescription = "Pedidos"
                    )
                }
            },
            label = { Text("Pedidos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ObsidianDark,
                selectedTextColor = GoldPrimary,
                indicatorColor = GoldPrimary,
                unselectedIconColor = TextSecondaryDark,
                unselectedTextColor = TextSecondaryDark
            ),
            modifier = Modifier.testTag("nav_item_orders")
        )

        // Tab 4: Dynamic Admin / Merchant or Notifications Tab
        when (currentRole) {
            UserRoleMode.ADMIN -> {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.ADMIN_PANEL,
                    onClick = { onNavigate(AppScreen.ADMIN_PANEL) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.ADMIN_PANEL) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                            contentDescription = "Panel Admin"
                        )
                    },
                    label = { Text("Admin") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ObsidianDark,
                        selectedTextColor = GoldPrimary,
                        indicatorColor = GoldPrimary,
                        unselectedIconColor = TextSecondaryDark,
                        unselectedTextColor = TextSecondaryDark
                    ),
                    modifier = Modifier.testTag("nav_item_admin")
                )
            }
            UserRoleMode.MERCHANT -> {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.MERCHANT_PANEL,
                    onClick = { onNavigate(AppScreen.MERCHANT_PANEL) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.MERCHANT_PANEL) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                            contentDescription = "Mi Negocio"
                        )
                    },
                    label = { Text("Negocio") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ObsidianDark,
                        selectedTextColor = EmeraldTrust,
                        indicatorColor = EmeraldTrust,
                        unselectedIconColor = TextSecondaryDark,
                        unselectedTextColor = TextSecondaryDark
                    ),
                    modifier = Modifier.testTag("nav_item_merchant")
                )
            }
            UserRoleMode.EXPLORER -> {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.NOTIFICATIONS,
                    onClick = { onNavigate(AppScreen.NOTIFICATIONS) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (unreadNotifCount > 0) {
                                    Badge(containerColor = RubyAlert, contentColor = Color.White) {
                                        Text("$unreadNotifCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (currentScreen == AppScreen.NOTIFICATIONS) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                                contentDescription = "Avisos"
                            )
                        }
                    },
                    label = { Text("Avisos") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ObsidianDark,
                        selectedTextColor = GoldPrimary,
                        indicatorColor = GoldPrimary,
                        unselectedIconColor = TextSecondaryDark,
                        unselectedTextColor = TextSecondaryDark
                    ),
                    modifier = Modifier.testTag("nav_item_notifs")
                )
            }
        }

        // Tab 5: Perfil
        NavigationBarItem(
            selected = currentScreen == AppScreen.USER_PROFILE,
            onClick = { onNavigate(AppScreen.USER_PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.USER_PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Perfil"
                )
            },
            label = { Text("Perfil") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ObsidianDark,
                selectedTextColor = GoldPrimary,
                indicatorColor = GoldPrimary,
                unselectedIconColor = TextSecondaryDark,
                unselectedTextColor = TextSecondaryDark
            ),
            modifier = Modifier.testTag("nav_item_profile")
        )
    }
}

@Composable
fun VerificationBadge(
    status: VerificationStatus,
    reputationScore: Int? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = when (status) {
            VerificationStatus.VERIFICADO_PUBLICADO -> EmeraldContainer
            VerificationStatus.PENDIENTE_REVISION -> GoldContainer
            VerificationStatus.DOCUMENTOS_REQUERIDOS -> Color(0xFF3B2D11)
            VerificationStatus.RECHAZADO -> Color(0xFF451111)
            VerificationStatus.SUSPENDIDO -> Color(0xFF2E2E3A)
        },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (status) {
                    VerificationStatus.VERIFICADO_PUBLICADO -> Icons.Default.Verified
                    VerificationStatus.PENDIENTE_REVISION -> Icons.Default.Pending
                    VerificationStatus.DOCUMENTOS_REQUERIDOS -> Icons.Default.Warning
                    VerificationStatus.RECHAZADO -> Icons.Default.Cancel
                    VerificationStatus.SUSPENDIDO -> Icons.Default.Block
                },
                contentDescription = null,
                tint = when (status) {
                    VerificationStatus.VERIFICADO_PUBLICADO -> EmeraldTrust
                    VerificationStatus.PENDIENTE_REVISION -> GoldPrimary
                    VerificationStatus.DOCUMENTOS_REQUERIDOS -> AmberPending
                    VerificationStatus.RECHAZADO -> RubyAlert
                    VerificationStatus.SUSPENDIDO -> TextSecondaryDark
                },
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (status == VerificationStatus.VERIFICADO_PUBLICADO && reputationScore != null) {
                    "Verificado Admin • $reputationScore/100"
                } else {
                    status.label
                },
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = when (status) {
                        VerificationStatus.VERIFICADO_PUBLICADO -> OnEmeraldContainer
                        VerificationStatus.PENDIENTE_REVISION -> OnGoldContainer
                        VerificationStatus.DOCUMENTOS_REQUERIDOS -> AmberPending
                        VerificationStatus.RECHAZADO -> Color(0xFFFCA5A5)
                        VerificationStatus.SUSPENDIDO -> TextSecondaryDark
                    }
                )
            )
        }
    }
}

fun getCategoryIcon(category: BusinessCategory): ImageVector {
    return when (category) {
        BusinessCategory.BARBERIA -> Icons.Default.ContentCut
        BusinessCategory.REPOSTERIA -> Icons.Default.Cake
        BusinessCategory.BODEGA -> Icons.Default.Storefront
        BusinessCategory.CAFETERIA -> Icons.Default.Coffee
        BusinessCategory.RESTAURANTE -> Icons.Default.Restaurant
        BusinessCategory.FARMACIA -> Icons.Default.LocalPharmacy
        BusinessCategory.GOURMET -> Icons.Default.WineBar
    }
}
