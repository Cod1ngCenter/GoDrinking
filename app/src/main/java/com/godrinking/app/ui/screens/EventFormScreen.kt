package com.godrinking.app.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godrinking.app.data.PackageType
import com.godrinking.app.ui.components.LabeledTextField
import com.godrinking.app.ui.components.ScreenTopBar
import com.godrinking.app.ui.theme.*

private val availableServices = listOf(
    "Bartender Padrão", "Bartender Premium", "Anão Coqueteleiro",
    "DJ", "Decoração de Balcão", "Transporte de Equipamentos", "Assessoria de Bebidas"
)

@Composable
fun EventFormScreen(onBack: () -> Unit) {
    var eventName         by remember { mutableStateOf("") }
    var eventType         by remember { mutableStateOf("Casamento") }
    var selectedClient    by remember { mutableStateOf("") }
    var people            by remember { mutableStateOf("") }
    var location          by remember { mutableStateOf("") }
    var date              by remember { mutableStateOf("") }
    var time              by remember { mutableStateOf("") }
    var selectedPackage   by remember { mutableStateOf(PackageType.SMIRNOFF) }
    var selectedServices  by remember { mutableStateOf(setOf<String>()) }
    var observations      by remember { mutableStateOf("") }
    var extraCosts        by remember { mutableStateOf("0") }

    val eventTypes    = listOf("Casamento", "Formatura", "Corporativo", "Aniversário", "Outro")
    val clientOptions = listOf("Maria Silva", "João Santos", "Empresa XYZ LTDA", "Ana Costa", "Pedro Lima")

    val peopleCount = people.toIntOrNull() ?: 0
    val pkgTotal    = peopleCount * selectedPackage.pricePerPerson
    val svcTotal    = selectedServices.size * 400
    val finalValue  = pkgTotal + svcTotal + (extraCosts.toIntOrNull() ?: 0)

    Scaffold(
        topBar = { ScreenTopBar(title = "Novo Evento", onBack = onBack) }
    ) { padding ->
        LazyColumn(
            modifier        = Modifier.fillMaxSize().padding(padding),
            contentPadding  = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Dados do evento ────────────────────────────────────────────
            item {
                EventFormSection(
                    title = "Dados do Evento",
                    icon  = Icons.Default.Event
                ) {
                    LabeledTextField("Nome do Evento *", eventName, { eventName = it }, "Ex: Casamento Maria e João")
                    Spacer(Modifier.height(12.dp))

                    // Tipo do evento
                    Text("Tipo de Evento", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
                    ExposedDropdownField(
                        value     = eventType,
                        options   = eventTypes,
                        onSelect  = { eventType = it }
                    )
                    Spacer(Modifier.height(12.dp))

                    Text("Cliente Vinculado *", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
                    ExposedDropdownField(
                        value     = selectedClient.ifBlank { "Selecione um cliente" },
                        options   = clientOptions,
                        onSelect  = { selectedClient = it }
                    )
                    Spacer(Modifier.height(12.dp))
                    LabeledTextField("Número de Pessoas", people, { people = it }, "200")
                }
            }

            // ── Local ──────────────────────────────────────────────────────
            item {
                EventFormSection(title = "Local do Evento", icon = Icons.Default.LocationOn) {
                    LabeledTextField("Nome do Local *", location, { location = it }, "Ex: Espaço Império")
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            LabeledTextField("Data *", date, { date = it }, "dd/mm/aaaa")
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            LabeledTextField("Horário *", time, { time = it }, "HH:mm")
                        }
                    }
                }
            }

            // ── Pacote ─────────────────────────────────────────────────────
            item {
                EventFormSection(title = "Pacote Escolhido *", icon = Icons.Default.Inventory) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PackageType.values().forEach { pkg ->
                            val isSelected = selectedPackage == pkg
                            val (borderColor, activeBg, valueColor) = when (pkg) {
                                PackageType.ORLOFF   -> Triple(BlueInfo,     BlueInfoBg,     BlueInfo)
                                PackageType.SMIRNOFF -> Triple(AmberAccent,  AmberAccentBg,  AmberAccent)
                                PackageType.ABSOLUT  -> Triple(PurpleAccent, PurpleAccentBg, PurpleAccent)
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(2.dp, if (isSelected) borderColor else MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                                    .background(if (isSelected) activeBg else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { selectedPackage = pkg }
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier          = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier         = Modifier.size(18.dp).clip(CircleShape).border(2.dp, if (isSelected) valueColor else MaterialTheme.colorScheme.onSurfaceVariant, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(valueColor))
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(pkg.label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                        Text(
                                            "R$ ${pkg.pricePerPerson}/pessoa" + if (peopleCount > 0) " · Total: R$ ${peopleCount * pkg.pricePerPerson}" else "",
                                            fontSize = 12.sp,
                                            color    = if (isSelected) valueColor else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    val badge = when (pkg) { PackageType.ORLOFF -> "Básico"; PackageType.SMIRNOFF -> "Inter."; PackageType.ABSOLUT -> "Premium" }
                                    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(valueColor.copy(0.15f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                                        Text(badge, color = valueColor, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Serviços solicitados ───────────────────────────────────────
            item {
                EventFormSection(title = "Serviços Solicitados", icon = Icons.Default.Build) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        availableServices.forEach { svc ->
                            val isSel = selectedServices.contains(svc)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, if (isSel) PrimaryRed else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    .background(if (isSel) PrimaryRed.copy(0.08f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        selectedServices = if (isSel) selectedServices - svc else selectedServices + svc
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(svc, fontSize = 13.sp)
                                Checkbox(
                                    checked         = isSel,
                                    onCheckedChange = {
                                        selectedServices = if (isSel) selectedServices - svc else selectedServices + svc
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = PrimaryRed),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ── Observações ────────────────────────────────────────────────
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Observações", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
                        OutlinedTextField(
                            value         = observations,
                            onValueChange = { observations = it },
                            placeholder   = { Text("Notas, pedidos especiais, restrições...", fontSize = 13.sp) },
                            modifier      = Modifier.fillMaxWidth(),
                            shape         = RoundedCornerShape(10.dp),
                            minLines      = 3,
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor    = PrimaryRed,
                                focusedContainerColor    = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor  = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        )
                    }
                }
            }

            // ── Custos extras ──────────────────────────────────────────────
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp)) {
                    Row(
                        modifier              = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AttachMoney, null, tint = PrimaryRed, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Custos / Descontos Extras", fontSize = 14.sp)
                        }
                        OutlinedTextField(
                            value         = extraCosts,
                            onValueChange = { extraCosts = it },
                            modifier      = Modifier.width(110.dp),
                            singleLine    = true,
                            shape         = RoundedCornerShape(8.dp),
                            textStyle     = LocalTextStyle.current.copy(textAlign = TextAlign.End, fontSize = 14.sp),
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor    = PrimaryRed,
                                focusedContainerColor    = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor  = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        )
                    }
                }
            }

            // ── Valor final ────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(PrimaryRed, PrimaryRedDarker)))
                        .padding(20.dp)
                ) {
                    Column {
                        Text("Valor Final do Evento", color = Color.White.copy(0.8f), fontSize = 13.sp)
                        Text(
                            "R$ ${"%.2f".format(finalValue.toDouble())}",
                            color      = Color.White,
                            fontSize   = 30.sp,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.padding(vertical = 4.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (peopleCount > 0) {
                                SummaryRow("Pacote (${peopleCount} pessoas)", "R$ $pkgTotal")
                            }
                            if (selectedServices.isNotEmpty()) {
                                SummaryRow("Serviços (${selectedServices.size}x)", "R$ $svcTotal")
                            }
                            val extras = extraCosts.toIntOrNull() ?: 0
                            if (extras != 0) {
                                SummaryRow("Extras", "R$ $extras")
                            }
                        }
                    }
                }
            }

            // ── Botões ─────────────────────────────────────────────────────
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(12.dp)) { Text("Cancelar") }
                    Button(
                        onClick  = { /* salvar evento */ onBack() },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                        enabled  = eventName.isNotBlank()
                    ) {
                        Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Salvar Evento", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun EventFormSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 14.dp)) {
                Icon(icon, null, tint = PrimaryRed, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
            content()
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White.copy(0.8f), fontSize = 12.sp)
        Text(value, color = Color.White.copy(0.8f), fontSize = 12.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdownField(value: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value          = value,
            onValueChange  = {},
            readOnly       = true,
            trailingIcon   = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier       = Modifier.menuAnchor().fillMaxWidth(),
            shape          = RoundedCornerShape(10.dp),
            singleLine     = true,
            colors         = OutlinedTextFieldDefaults.colors(
                focusedBorderColor    = PrimaryRed,
                focusedContainerColor    = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor  = MaterialTheme.colorScheme.surfaceVariant,
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text    = { Text(opt, fontSize = 14.sp) },
                    onClick = { onSelect(opt); expanded = false }
                )
            }
        }
    }
}
