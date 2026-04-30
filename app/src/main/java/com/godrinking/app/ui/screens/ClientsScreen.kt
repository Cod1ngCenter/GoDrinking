package com.godrinking.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godrinking.app.data.Client
import com.godrinking.app.data.ClientStatus
import com.godrinking.app.data.SampleData
import com.godrinking.app.ui.theme.*

@Composable
fun ClientsScreen(onViewDetails: (Int) -> Unit = {}) {
    var search          by remember { mutableStateOf("") }
    var selectedFilter  by remember { mutableStateOf("Todos") }

    val filters = listOf("Todos", "Em andamento", "Convertidos", "Perdidos")

    val filteredClients = SampleData.clients.filter { client ->
        val matchSearch = client.name.contains(search, ignoreCase = true) ||
                          client.document.contains(search, ignoreCase = true)
        val matchFilter = when (selectedFilter) {
            "Em andamento" -> client.status == ClientStatus.EM_ANDAMENTO
            "Convertidos"  -> client.status == ClientStatus.CONVERTIDO
            "Perdidos"     -> client.status == ClientStatus.PERDIDO
            else           -> true
        }
        matchSearch && matchFilter
    }

    LazyColumn(
        modifier        = Modifier.fillMaxSize(),
        contentPadding  = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search bar
        item {
            OutlinedTextField(
                value         = search,
                onValueChange = { search = it },
                placeholder   = { Text("Buscar por nome ou CPF/CNPJ...", fontSize = 13.sp) },
                leadingIcon   = { Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp)) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                singleLine    = true,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor    = PrimaryRed,
                    unfocusedBorderColor  = MaterialTheme.colorScheme.outline,
                    focusedContainerColor    = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor  = MaterialTheme.colorScheme.surfaceVariant,
                )
            )
        }

        // Filter chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick  = { selectedFilter = filter },
                        label    = { Text(filter, fontSize = 13.sp) },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryRed,
                            selectedLabelColor     = Color.White
                        )
                    )
                }
            }
        }

        // Client list
        if (filteredClients.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PersonOff, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f))
                        Spacer(Modifier.height(12.dp))
                        Text("Nenhum cliente encontrado", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(filteredClients) { client ->
                ClientCard(client = client, onViewDetails = { onViewDetails(client.id) })
            }
        }
    }
}

@Composable
private fun ClientCard(client: Client, onViewDetails: () -> Unit) {
    val (statusColor, statusBg) = when (client.status) {
        ClientStatus.CONVERTIDO   -> GreenSuccess  to GreenSuccessBg
        ClientStatus.EM_ANDAMENTO -> YellowWarning to YellowWarningBg
        ClientStatus.PERDIDO      -> RedDanger     to RedDangerBg
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Nome + Status
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(client.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text(client.document, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(client.status.label, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Contatos
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(8.dp))
                Text(client.phone, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(8.dp))
                Text(client.email, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Cadastrado em ${client.registeredAt}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(onClick = onViewDetails, contentPadding = PaddingValues(0.dp)) {
                    Text("Ver detalhes →", color = PrimaryRed, fontSize = 13.sp)
                }
            }
        }
    }
}
