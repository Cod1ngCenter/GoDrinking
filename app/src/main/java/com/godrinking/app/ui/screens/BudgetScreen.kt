package com.godrinking.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godrinking.app.ui.theme.*

// ── Dados dos pacotes ─────────────────────────────────────────────────────────

private data class BudgetPackage(
    val id: String, val name: String, val badge: String,
    val pricePerPerson: Int, val borderColor: Color,
    val activeBg: Color, val badgeColor: Color, val valueColor: Color,
    val includes: List<String>
)

private val PACKAGES = listOf(
    BudgetPackage(
        "orloff", "Pacote Orloff", "Básico", 35,
        BlueInfo, BlueInfoBg, BlueInfo, BlueInfo,
        listOf("Vodka Orloff 1L (ilimitada)", "Mixer básico (água tônica, suco)", "Copos descartáveis", "Bartender por até 4h")
    ),
    BudgetPackage(
        "smirnoff", "Pacote Smirnoff", "Intermediário", 55,
        AmberAccent, AmberAccentBg, AmberAccent, AmberAccent,
        listOf("Vodka Smirnoff 1L (ilimitada)", "Drinks elaborados no cardápio", "Taças de vidro inclusas", "Bartender por até 5h", "Gelo e frutas inclusas")
    ),
    BudgetPackage(
        "absolut", "Pacote Absolut", "Premium", 85,
        PurpleAccent, PurpleAccentBg, PurpleAccent, PurpleAccent,
        listOf("Vodka Absolut 1L (ilimitada)", "Cardápio gourmet de drinks", "Taças premium + decoração", "Bartender premium por até 6h", "Gelo, frutas e insumos premium", "Relatório pós-evento")
    ),
)

private data class ExtraService(val id: String, val name: String, val price: Int)

private val EXTRA_SERVICES = listOf(
    ExtraService("balcao",          "Aluguel de Balcão Portátil",    500),
    ExtraService("copos_extra",     "Copos/Taças Extras (kit 50)",   150),
    ExtraService("bartender_extra", "Bartender Adicional (+2h)",     300),
    ExtraService("anao",            "Anão Coqueteleiro",             800),
    ExtraService("dj",              "DJ (serviço parceiro)",        1200),
    ExtraService("transporte",      "Frete / Transporte",            250),
)

// ── BudgetScreen ──────────────────────────────────────────────────────────────

@Composable
fun BudgetScreen() {
    var selectedPkg    by remember { mutableStateOf("smirnoff") }
    var people         by remember { mutableIntStateOf(100) }
    var selectedExtras by remember { mutableStateOf(setOf<String>()) }
    var discount       by remember { mutableStateOf("0") }
    var expandedPkg    by remember { mutableStateOf<String?>(null) }

    val pkg         = PACKAGES.first { it.id == selectedPkg }
    val pkgTotal    = people * pkg.pricePerPerson
    val extrasTotal = EXTRA_SERVICES.filter { selectedExtras.contains(it.id) }.sumOf { it.price }
    val subtotal    = pkgTotal + extrasTotal
    val total       = (subtotal - (discount.toIntOrNull() ?: 0)).coerceAtLeast(0)

    LazyColumn(
        modifier        = Modifier.fillMaxSize(),
        contentPadding  = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── Banner total ──────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(PrimaryRed, PrimaryRedDarker)))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Valor Total do Orçamento", color = Color.White.copy(0.8f), fontSize = 13.sp)
                    Text(
                        "R$ %,.2f".format(total.toDouble()).replace(",", "X").replace(".", ",").replace("X", "."),
                        color      = Color.White,
                        fontSize   = 38.sp,
                        fontWeight = FontWeight.Bold,
                        modifier   = Modifier.padding(vertical = 4.dp)
                    )
                    Text("$people pessoas · ${pkg.name}", color = Color.White.copy(0.7f), fontSize = 12.sp)
                }
            }
        }

        // ── Número de pessoas ─────────────────────────────────────────────
        item {
            SectionCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, null, tint = PrimaryRed, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Número de Pessoas", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = { if (people > 10) people -= 10 },
                        modifier = Modifier.size(44.dp),
                        colors   = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) { Text("−", fontSize = 20.sp, fontWeight = FontWeight.Bold) }

                    OutlinedTextField(
                        value         = people.toString(),
                        onValueChange = { people = it.toIntOrNull()?.coerceAtLeast(1) ?: people },
                        modifier      = Modifier.weight(1f),
                        textStyle     = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 18.sp),
                        singleLine    = true,
                        shape         = RoundedCornerShape(10.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor    = PrimaryRed,
                            unfocusedBorderColor  = MaterialTheme.colorScheme.outline,
                            focusedContainerColor    = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor  = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    )

                    FilledIconButton(
                        onClick  = { people += 10 },
                        modifier = Modifier.size(44.dp),
                        colors   = IconButtonDefaults.filledIconButtonColors(containerColor = PrimaryRed)
                    ) { Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                }
            }
        }

        // ── Seleção de pacote ─────────────────────────────────────────────
        item {
            SectionCard {
                Text("Escolha o Pacote", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PACKAGES.forEach { p ->
                        val isSelected = selectedPkg == p.id
                        val isExpanded = expandedPkg == p.id

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 2.dp,
                                    color = if (isSelected) p.borderColor else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .background(if (isSelected) p.activeBg else Color.Transparent)
                        ) {
                            Row(
                                modifier          = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Radio button manual
                                Box(
                                    modifier         = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, if (isSelected) p.valueColor else MaterialTheme.colorScheme.onSurfaceVariant, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(p.valueColor)
                                        )
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(p.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    Text(
                                        "R$ ${p.pricePerPerson}/pessoa · Total: R$ ${people * p.pricePerPerson}",
                                        fontSize = 12.sp,
                                        color    = if (isSelected) p.valueColor else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(p.badgeColor.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(p.badge, color = p.badgeColor, fontSize = 10.sp)
                                }
                                Spacer(Modifier.width(8.dp))
                                IconButton(
                                    onClick  = { expandedPkg = if (isExpanded) null else p.id },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        null,
                                        modifier = Modifier.size(18.dp),
                                        tint     = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Selecionar ao tocar no card
                            LaunchedEffect(Unit) {}
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPkg = p.id }
                                    .padding(0.dp)
                            ) {}

                            if (isExpanded) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Inclui:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    p.includes.forEach { inc ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp), tint = p.valueColor)
                                            Spacer(Modifier.width(8.dp))
                                            Text(inc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }

                        // Clique para selecionar
                        DisposableEffect(p.id) {
                            onDispose {}
                        }
                    }
                }
            }
        }

        // ── Serviços adicionais ───────────────────────────────────────────
        item {
            SectionCard {
                Text("Serviços Adicionais", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    EXTRA_SERVICES.forEach { service ->
                        val isSelected = selectedExtras.contains(service.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) PrimaryRed else MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(10.dp)
                                )
                                .background(if (isSelected) PrimaryRed.copy(0.08f) else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    selectedExtras = if (isSelected)
                                        selectedExtras - service.id
                                    else
                                        selectedExtras + service.id
                                }
                                .padding(12.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked  = isSelected,
                                    onCheckedChange = {
                                        selectedExtras = if (isSelected)
                                            selectedExtras - service.id
                                        else
                                            selectedExtras + service.id
                                    },
                                    colors   = CheckboxDefaults.colors(
                                        checkedColor = PrimaryRed,
                                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(service.name, fontSize = 13.sp)
                            }
                            Text(
                                "+ R$ ${service.price}",
                                fontSize = 13.sp,
                                color    = if (isSelected) PrimaryRed else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // ── Desconto ──────────────────────────────────────────────────────
        item {
            SectionCard {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Desconto (R$)", fontSize = 14.sp)
                    OutlinedTextField(
                        value         = discount,
                        onValueChange = { discount = it },
                        modifier      = Modifier.width(120.dp),
                        singleLine    = true,
                        shape         = RoundedCornerShape(10.dp),
                        textStyle     = LocalTextStyle.current.copy(textAlign = TextAlign.End, fontSize = 14.sp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor    = PrimaryRed,
                            unfocusedBorderColor  = MaterialTheme.colorScheme.outline,
                            focusedContainerColor    = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor  = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    )
                }
            }
        }

        // ── Resumo ────────────────────────────────────────────────────────
        item {
            SectionCard {
                Text("Resumo do Orçamento", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BudgetRow("Pacote (${pkg.name})", "R$ $pkgTotal")
                    selectedExtras.forEach { id ->
                        val s = EXTRA_SERVICES.first { it.id == id }
                        BudgetRow(s.name, "R$ ${s.price}")
                    }
                    val disc = discount.toIntOrNull() ?: 0
                    if (disc > 0) BudgetRow("Desconto", "− R$ $disc", green = true)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total", fontWeight = FontWeight.SemiBold)
                        Text("R$ $total", color = PrimaryRed, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        // ── Botões de ação ────────────────────────────────────────────────
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick  = {},
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Download, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Baixar PDF")
                }
                Button(
                    onClick        = {},
                    modifier       = Modifier.weight(1f).height(52.dp),
                    shape          = RoundedCornerShape(12.dp),
                    colors         = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                ) {
                    Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Compartilhar")
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun BudgetRow(label: String, value: String, green: Boolean = false) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 13.sp, color = if (green) GreenSuccess else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}
