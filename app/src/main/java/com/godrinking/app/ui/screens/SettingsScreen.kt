package com.godrinking.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godrinking.app.R
import com.godrinking.app.ui.components.ScreenTopBar
import com.godrinking.app.ui.theme.*

@Composable
fun SettingsScreen(
    currentTheme: String,
    onThemeChange: (String) -> Unit,
    onBack: () -> Unit
) {
    val notifStates = remember {
        mutableStateMapOf(
            "Novos eventos"            to true,
            "Estoque crítico"          to true,
            "Lembretes de pagamento"   to true,
            "Atualizações do sistema"  to false,
        )
    }

    Scaffold(
        topBar = { ScreenTopBar(title = "Configurações", onBack = onBack) }
    ) { padding ->
        LazyColumn(
            modifier        = Modifier.fillMaxSize().padding(padding),
            contentPadding  = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Aparência ──────────────────────────────────────────────────
            item {
                SettingSection(title = "APARÊNCIA") {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tema do aplicativo", fontSize = 14.sp, modifier = Modifier.padding(bottom = 12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Dark mode card
                            ThemeOptionCard(
                                label      = "Escuro",
                                icon       = Icons.Default.NightsStay,
                                isSelected = currentTheme == "dark",
                                onClick    = { onThemeChange("dark") },
                                modifier   = Modifier.weight(1f)
                            )
                            // Light mode card
                            ThemeOptionCard(
                                label      = "Claro",
                                icon       = Icons.Default.LightMode,
                                isSelected = currentTheme == "light",
                                onClick    = { onThemeChange("light") },
                                modifier   = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ── Notificações ───────────────────────────────────────────────
            item {
                SettingSection(title = "NOTIFICAÇÕES") {
                    Column {
                        notifStates.entries.forEachIndexed { idx, (label, enabled) ->
                            NotifRow(
                                label       = label,
                                description = when (label) {
                                    "Novos eventos"          -> "Alertas de eventos cadastrados"
                                    "Estoque crítico"        -> "Quando itens atingem nível crítico"
                                    "Lembretes de pagamento" -> "Pagamentos pendentes de clientes"
                                    else                     -> "Novas versões e melhorias"
                                },
                                checked     = enabled,
                                onToggle    = { notifStates[label] = it }
                            )
                            if (idx < notifStates.size - 1) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
                            }
                        }
                    }
                }
            }

            // ── Segurança ──────────────────────────────────────────────────
            item {
                SettingSection(title = "SEGURANÇA") {
                    Column {
                        SecurityRow(
                            icon        = Icons.Default.Lock,
                            label       = "Alterar senha",
                            description = "Última alteração há 30 dias"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
                        SecurityRow(
                            icon        = Icons.Default.PhoneAndroid,
                            label       = "Autenticação em 2 etapas",
                            description = "Não configurado"
                        )
                    }
                }
            }

            // ── Sobre ──────────────────────────────────────────────────────
            item {
                SettingSection(title = "SOBRE") {
                    Column {
                        Row(
                            modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.width(12.dp))
                                Text("Versão do aplicativo", fontSize = 14.sp)
                            }
                            Text("1.0.0 – Protótipo", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
                        Box(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier          = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PrimaryRed.copy(0.08f))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier         = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp))
                                        .background(Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.app_logo),
                                        contentDescription = "App Logo",
                                        modifier = Modifier.fillMaxSize().padding(4.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("Go Drinking!", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    Text("Open bar & eventos personalizados", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

// ── Componentes auxiliares ────────────────────────────────────────────────────

@Composable
private fun SettingSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            title,
            fontSize  = 11.sp,
            fontWeight = FontWeight.Medium,
            color      = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier   = Modifier.padding(bottom = 6.dp, start = 4.dp)
        )
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(12.dp),
            colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun ThemeOptionCard(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        PrimaryRed.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    }

    val borderColor = if (isSelected) {
        PrimaryRed
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    }

    Box(
        modifier = modifier
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier            = Modifier.fillMaxSize()
        ) {
            Icon(
                icon, null, 
                modifier = Modifier.size(28.dp), 
                tint = if (isSelected) PrimaryRed else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                label, 
                fontSize = 14.sp, 
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) PrimaryRed else MaterialTheme.colorScheme.onSurface
            )
            
            if (isSelected) {
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(PrimaryRed)
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text("Ativo", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun NotifRow(label: String, description: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.NotificationsNone, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(label, fontSize = 14.sp)
                Text(description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(
            checked         = checked,
            onCheckedChange = onToggle,
            colors          = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryRed)
        )
    }
}

@Composable
private fun SecurityRow(icon: ImageVector, label: String, description: String) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(label, fontSize = 14.sp)
                Text(description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(Icons.Default.ChevronRight, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
