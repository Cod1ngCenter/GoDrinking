package com.godrinking.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.godrinking.app.data.StockCategory
import com.godrinking.app.ui.components.LabeledTextField
import com.godrinking.app.ui.components.ScreenTopBar
import com.godrinking.app.ui.theme.*

@Composable
fun StockFormScreen(onBack: () -> Unit) {
    var name       by remember { mutableStateOf("") }
    var category   by remember { mutableStateOf(StockCategory.BEBIDAS) }
    var quantity   by remember { mutableIntStateOf(0) }
    var threshold  by remember { mutableStateOf("10") }
    var estValue   by remember { mutableStateOf("") }

    val ratio  = if ((threshold.toIntOrNull() ?: 0) > 0) quantity.toFloat() / (threshold.toInt()) else 0f
    val (statusLabel, statusColor) = when {
        ratio <= 0.5f -> "Crítico" to RedDanger
        ratio <= 1f   -> "Atenção" to YellowWarning
        else          -> "Normal"  to GreenSuccess
    }

    Scaffold(
        topBar = { ScreenTopBar(title = "Novo Item no Estoque", onBack = onBack) }
    ) { padding ->
        LazyColumn(
            modifier        = Modifier.fillMaxSize().padding(padding),
            contentPadding  = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        LabeledTextField(
                            label = "Nome do Item *",
                            value = name,
                            onValueChange = { name = it },
                            placeholder = "Ex: Vodka Absolut"
                        )

                        // Categoria
                        Column {
                            Text("Categoria *", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
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
                        }

                        // Quantidade
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Quantidade", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    FilledTonalIconButton(onClick = { if (quantity > 0) quantity-- }, modifier = Modifier.size(40.dp)) {
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
                                        modifier = Modifier.size(40.dp),
                                        colors   = IconButtonDefaults.filledIconButtonColors(containerColor = PrimaryRed)
                                    ) { Text("+", color = Color.White, fontWeight = FontWeight.Bold) }
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                LabeledTextField(
                                    label = "Nível Crítico",
                                    value = threshold,
                                    onValueChange = { threshold = it },
                                    placeholder = "10"
                                )
                            }
                        }

                        LabeledTextField(
                            label = "Valor Estimado (R$)",
                            value = estValue,
                            onValueChange = { estValue = it },
                            placeholder = "0.00"
                        )

                        // Preview do status
                        Row(
                            modifier              = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text("Status previsto:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(statusColor.copy(0.15f)).padding(horizontal = 10.dp, vertical = 6.dp)
                            ) { Text(statusLabel, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Medium) }
                        }
                    }
                }
            }

            // Botões
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedButton(
                        onClick  = onBack,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape    = RoundedCornerShape(12.dp)
                    ) { Text("Cancelar") }
                    Button(
                        onClick  = {
                            /* salvar item */
                            onBack()
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                        enabled  = name.isNotBlank()
                    ) {
                        Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Salvar", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
