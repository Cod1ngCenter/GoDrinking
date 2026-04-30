package com.godrinking.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godrinking.app.data.ClientStatus
import com.godrinking.app.data.SampleData
import com.godrinking.app.ui.components.ScreenTopBar
import com.godrinking.app.ui.theme.*

@Composable
fun ClientDetailScreen(
    clientId: Int,
    canDelete: Boolean,
    onBack: () -> Unit
) {
    val client = SampleData.clients.firstOrNull { it.id == clientId } ?: SampleData.clients.first()

    val linkedEvents = SampleData.events.filter { it.client == client.name }

    val (statusColor, statusBg) = when (client.status) {
        ClientStatus.CONVERTIDO   -> GreenSuccess  to GreenSuccessBg
        ClientStatus.EM_ANDAMENTO -> YellowWarning to YellowWarningBg
        ClientStatus.PERDIDO      -> RedDanger     to RedDangerBg
    }

    Scaffold(
        topBar = { ScreenTopBar(title = "Detalhes do Cliente", onBack = onBack) }
    ) { padding ->
        LazyColumn(
            modifier        = Modifier.fillMaxSize().padding(padding),
            contentPadding  = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Informações principais ────────────────────────────────────
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(client.name, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                                Text(client.document, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                            }
                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(statusBg).padding(horizontal = 10.dp, vertical = 4.dp)) {
                                Text(client.status.label, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        ContactRow(Icons.Default.Phone, client.phone)
                        Spacer(Modifier.height(8.dp))
                        ContactRow(Icons.Default.Email, client.email)
                        client.address?.let { addr ->
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(18.dp).padding(top = 1.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text("${addr.street}, ${addr.number}", fontSize = 14.sp)
                                    Text("${addr.neighborhood}, ${addr.city}/${addr.state}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("CEP: ${addr.zip}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(10.dp))
                            Text("Cadastrado em ${client.registeredAt}", fontSize = 14.sp)
                        }
                    }
                }
            }

            // ── Eventos vinculados ────────────────────────────────────────
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Eventos Vinculados", fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.padding(bottom = 10.dp))
                        if (linkedEvents.isEmpty()) {
                            Text("Nenhum evento vinculado", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 8.dp))
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                linkedEvents.forEach { event ->
                                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(12.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Column {
                                                Text(event.name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                                Text(
                                                    android.text.format.DateFormat.format("dd/MM/yyyy", event.date).toString(),
                                                    fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(GreenSuccessBg).padding(horizontal = 8.dp, vertical = 3.dp)) {
                                                Text("Confirmado", color = GreenSuccess, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Botões de ação ────────────────────────────────────────────
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(12.dp)) {
                        Text("Voltar")
                    }
                    Button(onClick = {}, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Editar")
                    }
                    if (canDelete) {
                        OutlinedButton(
                            onClick  = {},
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = RedDanger),
                            border   = ButtonDefaults.outlinedButtonBorder(true).copy(
                                brush = androidx.compose.ui.graphics.SolidColor(RedDanger.copy(0.5f))
                            )
                        ) {
                            Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(10.dp))
        Text(value, fontSize = 14.sp)
    }
}
