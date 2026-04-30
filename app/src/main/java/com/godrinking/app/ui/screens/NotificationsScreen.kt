package com.godrinking.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godrinking.app.data.Notification
import com.godrinking.app.data.SampleData
import com.godrinking.app.ui.components.ScreenTopBar
import com.godrinking.app.ui.theme.*

@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf("Todas") }
    val tabs = listOf("Todas", "Não lidas")

    val notifications = SampleData.notifications
    val filtered = when (selectedTab) {
        "Não lidas" -> notifications.filter { !it.read }
        else        -> notifications
    }

    Scaffold(
        topBar = { ScreenTopBar(title = "Notificações", onBack = onBack) }
    ) { padding ->
        LazyColumn(
            modifier        = Modifier.fillMaxSize().padding(padding),
            contentPadding  = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tabs.forEach { tab ->
                        FilterChip(
                            selected = selectedTab == tab,
                            onClick  = { selectedTab = tab },
                            label    = { Text(tab, fontSize = 13.sp) },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryRed,
                                selectedLabelColor     = Color.White
                            )
                        )
                    }
                }
            }

            if (filtered.isEmpty()) {
                item {
                    Box(
                        modifier         = Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.NotificationsNone, null,
                                modifier = Modifier.size(48.dp),
                                tint     = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f))
                            Spacer(Modifier.height(8.dp))
                            Text("Nenhuma notificação", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(filtered) { notif ->
                    NotificationCard(notification = notif)
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: Notification) {
    val (icon, iconColor) = when (notification.type) {
        "stock"  -> Icons.Default.Inventory     to RedDanger
        "event"  -> Icons.Default.CalendarMonth to BlueInfo
        "budget" -> Icons.Default.RequestQuote  to YellowWarning
        "client" -> Icons.Default.PersonAdd     to GreenSuccess
        else     -> Icons.Default.Notifications to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border    = if (!notification.read)
                        CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(PrimaryRed.copy(0.5f))
                        )
                    else null,
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier         = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(notification.title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    if (!notification.read) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryRed))
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(notification.message, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(notification.time, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
