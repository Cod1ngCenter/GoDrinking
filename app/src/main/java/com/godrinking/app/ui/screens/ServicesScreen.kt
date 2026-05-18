@file:OptIn(ExperimentalMaterial3Api::class)
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
import androidx.compose.ui.window.Dialog
import com.godrinking.app.data.Service
import com.godrinking.app.data.SampleData
import com.godrinking.app.ui.components.ScreenTopBar
import com.godrinking.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(onBack: () -> Unit) {
    var services       by remember { mutableStateOf(SampleData.services.toMutableList()) }
    var search         by remember { mutableStateOf("") }
    var activeCategory by remember { mutableStateOf("Todos") }
    var showForm       by remember { mutableStateOf(false) }
    var editingService by remember { mutableStateOf<Service?>(null) }

    val categories = listOf("Todos") + services.map { it.category }.distinct()

    val filtered = services.filter { s ->
        val matchSearch = s.name.contains(search, ignoreCase = true) ||
                          s.description.contains(search, ignoreCase = true)
        val matchCat    = activeCategory == "Todos" || s.category == activeCategory
        matchSearch && matchCat
    }
    val totalValue = services.sumOf { it.price }

    if (showForm) {
        ServiceFormDialog(
            service  = editingService,
            onSave   = { data ->
                services = if (editingService != null) {
                    services.map { if (it.id == editingService!!.id) data.copy(id = it.id) else it }.toMutableList()
                } else {
                    (services + data.copy(id = System.currentTimeMillis().toInt())).toMutableList()
                }
                showForm       = false
                editingService = null
            },
            onCancel = { showForm = false; editingService = null }
        )
    }

    Scaffold(
        topBar = {
            ScreenTopBar(title = "Serviços", onBack = onBack)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick          = { editingService = null; showForm = true },
                containerColor   = PrimaryRed,
                contentColor     = Color.White,
                shape            = RoundedCornerShape(360.dp)
            ) {
                Icon(Icons.Default.Add, "Novo Serviço")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier        = Modifier.fillMaxSize().padding(padding),
            contentPadding  = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Stats ──────────────────────────────────────────────────────
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(0.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(PrimaryRed), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Label, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Total de Serviços", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(services.size.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(0.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(GreenSuccess), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AttachMoney, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Valor Total Catálogo", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("R$ ${"%.0f".format(totalValue)}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ── Busca ──────────────────────────────────────────────────────
            item {
                OutlinedTextField(
                    value         = search,
                    onValueChange = { search = it },
                    placeholder   = { Text("Pesquisar serviços...", fontSize = 13.sp) },
                    leadingIcon   = { Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp)) },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    singleLine    = true,
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor    = PrimaryRed,
                        focusedContainerColor    = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor  = MaterialTheme.colorScheme.surface,
                    )
                )
            }

            // ── Categorias ─────────────────────────────────────────────────
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = activeCategory == cat,
                            onClick  = { activeCategory = cat },
                            label    = { Text(cat, fontSize = 12.sp) },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryRed,
                                selectedLabelColor     = Color.White
                            )
                        )
                    }
                }
            }

            // ── Lista ──────────────────────────────────────────────────────
            if (filtered.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.MiscellaneousServices, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f))
                            Spacer(Modifier.height(8.dp))
                            Text("Nenhum serviço encontrado", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(filtered) { service ->
                    ServiceCard(
                        service  = service,
                        onEdit   = { editingService = service; showForm = true },
                        onDelete = { services = services.filter { it.id != service.id }.toMutableList() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(service: Service, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(service.name, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(service.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp), tint = RedDanger)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(service.category, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("R$ ${"%.0f".format(service.price)}", color = PrimaryRed, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }
    }
}

// ── Formulário de serviço (Dialog) ────────────────────────────────────────────

@Composable
private fun ServiceFormDialog(
    service: Service?,
    onSave: (Service) -> Unit,
    onCancel: () -> Unit
) {
    val categories = listOf("Pessoal", "Entretenimento", "Decoração", "Logística", "Consultoria", "Outro")
    var name        by remember { mutableStateOf(service?.name ?: "") }
    var description by remember { mutableStateOf(service?.description ?: "") }
    var price       by remember { mutableStateOf((service?.price ?: 0.0).toString()) }
    var category    by remember { mutableStateOf(service?.category ?: "Pessoal") }

    Dialog(onDismissRequest = onCancel) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (service != null) "Editar Serviço" else "Novo Serviço", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = onCancel, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp))
                    }
                }
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome do Serviço *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), minLines = 2, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Preço (R$)") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Categoria", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        ExposedDropdownMenuBox(
                            expanded = false, onExpandedChange = {},
                        ) {
                            OutlinedTextField(
                                value = category, onValueChange = {}, readOnly = true,
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape    = RoundedCornerShape(10.dp),
                                colors   = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed)
                            )
                        }
                    }
                }
                // Categoria chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(selected = category == cat, onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryRed, selectedLabelColor = Color.White))
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(10.dp)) { Text("Cancelar") }
                    Button(
                        onClick  = {
                            if (name.isNotBlank()) {
                                onSave(Service(
                                    id          = service?.id ?: 0,
                                    name        = name,
                                    description = description,
                                    price       = price.toDoubleOrNull() ?: 0.0,
                                    category    = category
                                ))
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                        enabled  = name.isNotBlank()
                    ) {
                        Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Salvar")
                    }
                }
            }
        }
    }
}
