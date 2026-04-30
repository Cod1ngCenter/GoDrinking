package com.godrinking.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.godrinking.app.data.*
import com.godrinking.app.ui.theme.*

@Composable
fun StockScreen() {
    var items          by remember { mutableStateOf(SampleData.stockItems.toMutableList()) }
    var search         by remember { mutableStateOf("") }
    var activeCategory by remember { mutableStateOf("Todos") }
    var showForm       by remember { mutableStateOf(false) }
    var editingItem    by remember { mutableStateOf<StockItem?>(null) }

    val categories = listOf("Todos") + StockCategory.values().map { it.label }

    val filtered = items.filter { item ->
        val matchSearch = item.name.contains(search, ignoreCase = true)
        val matchCat    = activeCategory == "Todos" || item.category.label == activeCategory
        matchSearch && matchCat
    }

    val totalItems    = items.size
    val criticalItems = items.count { it.status == StockStatus.CRITICAL }
    val warningItems  = items.count { it.status == StockStatus.WARNING }

    if (showForm) {
        StockItemFormDialog(
            item     = editingItem,
            onSave   = { data ->
                if (editingItem != null) {
                    items = items.map { if (it.id == editingItem!!.id) data.copy(id = it.id) else it }.toMutableList()
                } else {
                    items = (items + data.copy(id = System.currentTimeMillis().toInt())).toMutableList()
                }
                showForm    = false
                editingItem = null
            },
            onCancel = { showForm = false; editingItem = null }
        )
    }

    LazyColumn(
        modifier        = Modifier.fillMaxSize(),
        contentPadding  = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Stats ─────────────────────────────────────────────────────────
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Total",   totalItems.toString(), BlueInfo,    Modifier.weight(1f))
                StatCard("Atenção", warningItems.toString(), YellowWarning, Modifier.weight(1f))
                StatCard("Crítico", criticalItems.toString(), RedDanger, Modifier.weight(1f))
            }
        }

        // ── Search + Adicionar ─────────────────────────────────────────────
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value         = search,
                    onValueChange = { search = it },
                    placeholder   = { Text("Pesquisar itens...", fontSize = 13.sp) },
                    leadingIcon   = { Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp)) },
                    modifier      = Modifier.weight(1f),
                    shape         = RoundedCornerShape(12.dp),
                    singleLine    = true,
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor    = PrimaryRed,
                        unfocusedBorderColor  = MaterialTheme.colorScheme.outline,
                        focusedContainerColor    = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor  = MaterialTheme.colorScheme.surface,
                    )
                )
                FilledIconButton(
                    onClick  = { editingItem = null; showForm = true },
                    modifier = Modifier.size(52.dp),
                    colors   = IconButtonDefaults.filledIconButtonColors(containerColor = PrimaryRed)
                ) { Icon(Icons.Default.Add, null, tint = Color.White) }
            }
        }

        // ── Filtros de categoria ───────────────────────────────────────────
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

        // ── Lista de itens ─────────────────────────────────────────────────
        if (filtered.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inventory2, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f))
                        Spacer(Modifier.height(8.dp))
                        Text("Nenhum item encontrado", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    }
                }
            }
        } else {
            items(filtered) { item ->
                StockItemCard(item = item, onEdit = { editingItem = item; showForm = true })
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier         = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Inventory, null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun StockItemCard(item: StockItem, onEdit: () -> Unit) {
    val (statusColor, statusBg) = when (item.status) {
        StockStatus.CRITICAL -> RedDanger     to RedDangerBg
        StockStatus.WARNING  -> YellowWarning to YellowWarningBg
        StockStatus.NORMAL   -> GreenSuccess  to GreenSuccessBg
    }
    val barProgress = (item.quantity.toFloat() / (item.threshold * 2f)).coerceIn(0f, 1f)

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
                    Text(item.name, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    Text(item.category.label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(statusBg).padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(item.status.label, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))

            StockInfoRow("Quantidade",      "${item.quantity} unidades",    statusColor)
            StockInfoRow("Nível crítico",   "${item.threshold} un.",        MaterialTheme.colorScheme.onSurfaceVariant)
            StockInfoRow("Valor Estimado",  "R$ %.2f".format(item.estimatedValue), MaterialTheme.colorScheme.onSurface)

            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress      = { barProgress },
                modifier      = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color         = statusColor,
                trackColor    = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Composable
private fun StockInfoRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 13.sp, color = valueColor)
    }
}

// ── Formulário de item (Dialog) ───────────────────────────────────────────────

@Composable
private fun StockItemFormDialog(
    item: StockItem?,
    onSave: (StockItem) -> Unit,
    onCancel: () -> Unit
) {
    var name       by remember { mutableStateOf(item?.name ?: "") }
    var category   by remember { mutableStateOf(item?.category ?: StockCategory.BEBIDAS) }
    var quantity   by remember { mutableIntStateOf(item?.quantity ?: 0) }
    var threshold  by remember { mutableStateOf((item?.threshold ?: 10).toString()) }
    var estValue   by remember { mutableStateOf((item?.estimatedValue ?: 0.0).toString()) }

    val ratio  = if ((threshold.toIntOrNull() ?: 0) > 0) quantity.toFloat() / (threshold.toInt()) else 0f
    val (statusLabel, statusColor) = when {
        ratio <= 0.5f -> "Crítico" to RedDanger
        ratio <= 1f   -> "Atenção" to YellowWarning
        else          -> "Normal"  to GreenSuccess
    }

    Dialog(onDismissRequest = onCancel) {
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(20.dp),
            colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(if (item != null) "Editar Item" else "Novo Item no Estoque", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = onCancel, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp))
                    }
                }

                OutlinedTextField(
                    value         = name,
                    onValueChange = { name = it },
                    label         = { Text("Nome do Item *") },
                    placeholder   = { Text("Ex: Vodka Absolut") },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(10.dp),
                    singleLine    = true,
                    colors        = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed)
                )

                // Categoria
                Text("Categoria *", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StockCategory.values().forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick  = { category = cat },
                            label    = { Text(cat.label, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryRed,
                                selectedLabelColor     = Color.White
                            )
                        )
                    }
                }

                // Quantidade
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Quantidade", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilledTonalIconButton(onClick = { if (quantity > 0) quantity-- }, modifier = Modifier.size(36.dp)) {
                                Text("−", fontWeight = FontWeight.Bold)
                            }
                            Text(
                                quantity.toString(),
                                modifier  = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium,
                                fontSize  = 16.sp
                            )
                            FilledIconButton(
                                onClick  = { quantity++ },
                                modifier = Modifier.size(36.dp),
                                colors   = IconButtonDefaults.filledIconButtonColors(containerColor = PrimaryRed)
                            ) { Text("+", color = Color.White, fontWeight = FontWeight.Bold) }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value         = threshold,
                            onValueChange = { threshold = it },
                            label         = { Text("Nível Crítico") },
                            modifier      = Modifier.fillMaxWidth(),
                            shape         = RoundedCornerShape(10.dp),
                            singleLine    = true,
                            colors        = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed)
                        )
                    }
                }

                OutlinedTextField(
                    value         = estValue,
                    onValueChange = { estValue = it },
                    label         = { Text("Valor Estimado (R$)") },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(10.dp),
                    singleLine    = true,
                    colors        = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed)
                )

                // Preview do status
                Row(
                    modifier              = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Status previsto:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(statusColor.copy(0.15f)).padding(horizontal = 8.dp, vertical = 4.dp)
                    ) { Text(statusLabel, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Medium) }
                }

                // Botões
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick  = onCancel,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape    = RoundedCornerShape(10.dp)
                    ) { Text("Cancelar") }
                    Button(
                        onClick  = {
                            if (name.isNotBlank()) {
                                onSave(
                                    StockItem(
                                        id             = item?.id ?: 0,
                                        name           = name,
                                        category       = category,
                                        quantity       = quantity,
                                        threshold      = threshold.toIntOrNull() ?: 10,
                                        estimatedValue = estValue.toDoubleOrNull() ?: 0.0
                                    )
                                )
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
