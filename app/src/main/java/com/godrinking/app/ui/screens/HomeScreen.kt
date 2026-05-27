package com.godrinking.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godrinking.app.navigation.Screen
import com.godrinking.app.ui.theme.*
import java.util.Calendar

// ── Dados de exemplo ──────────────────────────────────────────────────────────

private val stockAlerts = listOf(
    Triple("Copos Descartáveis", "Crítico",  RedDanger),
    Triple("Vodka Absolut",      "Atenção",  YellowWarning),
    Triple("Gelo",               "Crítico",  RedDanger),
)

private val upcomingEvents = listOf(
    listOf("Maria Silva",   "Casamento",   "Espaço Império",       "200", "05 Mai", "Absolut"),
    listOf("João Santos",   "Formatura",   "Hotel Plaza",          "150", "12 Mai", "Smirnoff"),
    listOf("Empresa XYZ",   "Corporativo", "Centro de Convenções", "300", "18 Mai", "Orloff"),
)

// ── HomeScreen ────────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    userName: String,
    activeWidgets: List<String>,
    onNavigate: (String) -> Unit = {}
) {
    LazyColumn(
        modifier            = Modifier.fillMaxSize(),
        contentPadding      = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner de boas-vindas
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(listOf(PrimaryRed, PrimaryRedDarker))
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Text("Olá, $userName! 👋", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Text("Bem-vindo ao Go Drinking!", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }

        if (activeWidgets.isEmpty()) {
            item { EmptyWidgetsMessage() }
            return@LazyColumn
        }

        // Métricas
        if (activeWidgets.contains("metrics")) {
            item {
                MetricsRow(modifier = Modifier.padding(horizontal = 16.dp).offset(y = (-12).dp))
            }
        }

        // Calendário
        if (activeWidgets.contains("calendar")) {
            item {
                CalendarWidget(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }

        // Próximos eventos
        if (activeWidgets.contains("upcoming_events")) {
            item {
                UpcomingEventsWidget(
                    modifier    = Modifier.padding(horizontal = 16.dp),
                    onVerTodos  = { onNavigate("events") }
                )
            }
        }

        // Alertas de estoque
        if (activeWidgets.contains("stock_alerts")) {
            item {
                StockAlertsWidget(
                    modifier         = Modifier.padding(horizontal = 16.dp),
                    onVerEstoque     = { onNavigate("stock") }
                )
            }
        }

        // Resumo financeiro
        if (activeWidgets.contains("financial_summary")) {
            item {
                FinancialSummaryWidget(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }

        // Ações rápidas
        if (activeWidgets.contains("quick_actions")) {
            item {
                QuickActionsWidget(
                    modifier   = Modifier.padding(horizontal = 16.dp),
                    onNavigate = onNavigate
                )
            }
        }
    }
}

// ── Widgets ───────────────────────────────────────────────────────────────────

@Composable
private fun EmptyWidgetsMessage() {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.BarChart,
            contentDescription = null,
            modifier           = Modifier.size(56.dp),
            tint               = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(Modifier.height(12.dp))
        Text("Nenhum widget ativo", color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        Text(
            "Toque no botão de personalização para ajustar",
            color    = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun MetricsRow(modifier: Modifier = Modifier) {
    val metrics = listOf(
        Triple("Orçamentos", "24",        PrimaryRed),
        Triple("Clientes",   "156",       PrimaryRedDark),
        Triple("R$/Mês",     "45.2k",     PrimaryRedDarker),
    )
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        metrics.forEach { (label, value, color) ->
            Card(
                modifier  = Modifier.weight(1f),
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Box(
                        modifier         = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.TrendingUp, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(value, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun CalendarWidget(modifier: Modifier = Modifier) {
    val calendar = Calendar.getInstance()
    val today    = calendar.get(Calendar.DAY_OF_MONTH)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Domingo
    val monthName = android.text.format.DateFormat.format("MMMM yyyy", calendar).toString()
    val eventDays = listOf(5, 12, 18, 25)
    val dayLabels = listOf("D","S","T","Q","Q","S","S")

    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                monthName.replaceFirstChar { it.uppercaseChar() },
                fontSize   = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(12.dp))

            // Cabeçalho dias da semana
            Row(modifier = Modifier.fillMaxWidth()) {
                dayLabels.forEach { d ->
                    Text(
                        d, modifier = Modifier.weight(1f),
                        textAlign  = TextAlign.Center,
                        fontSize   = 11.sp,
                        color      = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(4.dp))

            // Grid de dias
            val cells = firstDayOfWeek + daysInMonth
            val rows  = (cells + 6) / 7
            var day   = 1 - firstDayOfWeek

            repeat(rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(7) {
                        val d = day++
                        if (d in 1..daysInMonth) {
                            val hasEvent = eventDays.contains(d)
                            val isToday  = d == today
                            Box(
                                modifier         = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when {
                                            hasEvent -> PrimaryRed
                                            isToday  -> MaterialTheme.colorScheme.surfaceVariant
                                            else     -> Color.Transparent
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    d.toString(),
                                    fontSize = 11.sp,
                                    color    = if (hasEvent) Color.White
                                               else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UpcomingEventsWidget(modifier: Modifier = Modifier, onVerTodos: () -> Unit) {
    Column(modifier = modifier) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text("Próximos Eventos", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            TextButton(onClick = onVerTodos) {
                Text("Ver todos", color = PrimaryRed, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            upcomingEvents.forEach { e ->
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
                                Text(e[0], fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text(e[1], fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PrimaryRed.copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(e[4], color = PrimaryRed, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text("📍 ${e[2]}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("👥 ${e[3]}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.weight(1f))
                            Text(e[5], fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StockAlertsWidget(modifier: Modifier = Modifier, onVerEstoque: () -> Unit) {
    Column(modifier = modifier) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text("Alertas de Estoque", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            TextButton(onClick = onVerEstoque) {
                Text("Ver estoque", color = PrimaryRed, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(12.dp),
            colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            stockAlerts.forEachIndexed { index, (name, status, color) ->
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .then(
                            if (index < stockAlerts.size - 1)
                                Modifier.padding(bottom = 0.dp)
                            else Modifier
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Inventory, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(name, fontSize = 13.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(color.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(status, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
                if (index < stockAlerts.size - 1) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                }
            }
        }
    }
}

@Composable
private fun FinancialSummaryWidget(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text("Resumo Financeiro", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(12.dp),
            colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    FinancialItem("Receita",  "R$ 45.2k", GreenSuccess)
                    FinancialItem("Despesas", "R$ 18.7k", RedDanger)
                    FinancialItem("Lucro",    "R$ 26.5k", PrimaryRed)
                }
                Spacer(Modifier.height(12.dp))
                // Barra de progresso
                LinearProgressIndicator(
                    progress          = { 0.59f },
                    modifier          = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color             = GreenSuccess,
                    trackColor        = MaterialTheme.colorScheme.surfaceVariant,
                )
                Text(
                    "Margem: 59%",
                    fontSize  = 11.sp,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun FinancialItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 14.sp, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun QuickActionsWidget(modifier: Modifier = Modifier, onNavigate: (String) -> Unit) {
    Column(modifier = modifier) {
        Text("Ações Rápidas", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        val actions = listOf(
            Triple("Novo Evento",     Icons.Default.CalendarMonth, Screen.EventForm.route),
            Triple("Novo Orçamento",  Icons.Default.Calculate,     Screen.CreateBudget.route),
            Triple("Ver Estoque",     Icons.Default.Inventory,     Screen.Stock.route),
            Triple("Relatórios",      Icons.Default.BarChart,      Screen.Reports.route),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                actions.take(2).forEach { (label, icon, tab) ->
                    QuickActionCard(label, icon, tab, onNavigate)
                }
            }
            Column(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                actions.drop(2).forEach { (label, icon, tab) ->
                    QuickActionCard(label, icon, tab, onNavigate)
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    label: String,
    icon: ImageVector,
    tab: String,
    onNavigate: (String) -> Unit
) {
    Card(
        onClick   = { onNavigate(tab) },
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(
            containerColor = PrimaryRed.copy(alpha = 0.12f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = PrimaryRed, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                lineHeight = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    GoDrinkingTheme {
        HomeScreen(
            userName = "Ricardo",
            activeWidgets = listOf(
                "metrics",
                "calendar",
                "upcoming_events",
                "stock_alerts",
                "financial_summary",
                "quick_actions"
            )
        )
    }
}
