package com.godrinking.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godrinking.app.data.UserRole
import com.godrinking.app.ui.theme.*

@Composable
fun SideMenuContent(
    userName: String,
    userRole: UserRole,
    onClose: () -> Unit,
    onProfile: () -> Unit,
    onEvents: () -> Unit,
    onServices: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxHeight()
    ) {
        // ── Header ─────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(colors = listOf(PrimaryRed, PrimaryRedDarker))
                )
                .padding(24.dp)
        ) {
            Column {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector   = Icons.Default.Close,
                        contentDescription = "Fechar menu",
                        tint          = Color.White
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar com iniciais
                    val initials = userName
                        .split(" ")
                        .take(2)
                        .joinToString("") { it.take(1).uppercase() }
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initials, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(userName, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                            Icon(
                                imageVector   = Icons.Default.Shield,
                                contentDescription = null,
                                tint          = Color.White.copy(alpha = 0.8f),
                                modifier      = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(userRole.name, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // ── Menu Items ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(12.dp)
        ) {
            Text(
                "MENU",
                style    = MaterialTheme.typography.labelSmall,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            DrawerMenuItem(
                icon        = Icons.Default.Person,
                label       = "Meu Perfil",
                description = "Ver e editar seus dados",
                onClick     = { onProfile(); onClose() }
            )
            DrawerMenuItem(
                icon        = Icons.Default.CalendarMonth,
                label       = "Meus Eventos",
                description = "Calendário e eventos cadastrados",
                onClick     = { onEvents(); onClose() }
            )
            DrawerMenuItem(
                icon        = Icons.Default.MiscellaneousServices,
                label       = "Serviços",
                description = "Gerenciar catálogo de serviços",
                onClick     = { onServices(); onClose() }
            )
            DrawerMenuItem(
                icon        = Icons.Default.Settings,
                label       = "Configurações",
                description = "Tema, notificações e mais",
                onClick     = { onSettings(); onClose() }
            )

            HorizontalDivider(
                modifier          = Modifier.padding(vertical = 8.dp),
                color             = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )

            // Logout
            TextButton(
                onClick  = { onLogout(); onClose() },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors   = ButtonDefaults.textButtonColors(contentColor = RedDanger)
            ) {
                Row(
                    modifier            = Modifier.fillMaxWidth(),
                    verticalAlignment   = Alignment.CenterVertically
                ) {
                    Box(
                        modifier         = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(RedDangerBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sair",
                            tint               = RedDanger,
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("Sair da conta", fontSize = 14.sp)
                }
            }
        }

        // ── Footer ─────────────────────────────────────────────────────────
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Row(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment   = Alignment.CenterVertically
        ) {
            Box(
                modifier         = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Brush.linearGradient(listOf(PrimaryRed, PrimaryRedDarker))),
                contentAlignment = Alignment.Center
            ) {
                Text("GD", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Go Drinking!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                Text("v1.0.0 – Protótipo", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    description: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors   = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier         = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = label,
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier           = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                imageVector        = Icons.Default.ChevronRight,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(16.dp)
            )
        }
    }
}
