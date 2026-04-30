package com.godrinking.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godrinking.app.ui.theme.*
import kotlin.math.min

@Composable
fun ReportsScreen() {
    var selectedPeriod by remember { mutableStateOf("Mês Atual") }
    val periods = listOf("Mês Atual", "Últimos 7 dias", "Trimestre", "Ano")

    val weeklyData = listOf(12000f, 15000f, 8000f, 18000f)
    val weekLabels = listOf("Sem 1", "Sem 2", "Sem 3", "Sem 4")

    data class PieSegment(val name: String, val value: Int, val color: Color)
    val pieData = listOf(
        PieSegment("Casamentos",  45000, PrimaryRed),
        PieSegment("Formaturas",  28000, Color(0xFFEF4444)),
        PieSegment("Corporativos",35000, Color(0xFFF87171)),
        PieSegment("Outros",      12000, Color(0xFFFCA5A5)),
    )
    val total = pieData.sumOf { it.value }

    LazyColumn(
        modifier        = Modifier.fillMaxSize(),
        contentPadding  = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── Filtros de período ─────────────────────────────────────────────
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(periods) { period ->
                    FilterChip(
                        selected = selectedPeriod == period,
                        onClick  = { selectedPeriod = period },
                        label    = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (period == "Mês Atual") {
                                    Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(14.dp).padding(end = 2.dp))
                                }
                                Text(period, fontSize = 13.sp)
                            }
                        },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryRed,
                            selectedLabelColor     = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )
                }
            }
        }

        // ── Cards de métricas ─────────────────────────────────────────────
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Receita Total", "R$ 120k", GreenSuccess, Modifier.weight(1f))
                MetricCard("Lucro Líq.",   "R$ 45k",  BlueInfo,     Modifier.weight(1f))
                MetricCard("Despesas",     "R$ 75k",  RedDanger,    Modifier.weight(1f))
            }
        }

        // ── Gráfico de barras ─────────────────────────────────────────────
        item {
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Receita por Semana", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Spacer(Modifier.height(16.dp))

                    val maxVal = weeklyData.max()
                    val barColor   = PrimaryRed
                    val trackColor = MaterialTheme.colorScheme.surfaceVariant
                    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

                    Row(
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        weeklyData.forEachIndexed { i, value ->
                            Column(
                                modifier            = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    "R$${(value / 1000).toInt()}k",
                                    fontSize = 9.sp,
                                    color    = labelColor,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(value / maxVal)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(barColor)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        weekLabels.forEach { lbl ->
                            Text(lbl, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // ── Gráfico de pizza (por tipo de evento) ─────────────────────────
        item {
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Receita por Tipo de Evento", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier          = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Canvas donut chart
                        Box(
                            modifier         = Modifier.size(140.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val segments = pieData
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawDonutChart(segments.map { Pair(it.value.toFloat(), it.color) })
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total", fontSize = 10.sp, color = Color.Gray)
                                Text("R${total / 1000}k", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(Modifier.width(16.dp))

                        // Legenda
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            pieData.forEach { seg ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(10.dp).clip(CircleShape).background(seg.color)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(seg.name, fontSize = 12.sp)
                                        Text(
                                            "R$ ${seg.value / 1000}k · ${(seg.value * 100 / total)}%",
                                            fontSize = 10.sp,
                                            color    = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Botão exportar ─────────────────────────────────────────────────
        item {
            Button(
                onClick  = {},
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
            ) {
                Icon(Icons.Default.Download, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Baixar Relatório PDF", fontWeight = FontWeight.SemiBold)
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

// ── Donut chart com Canvas ────────────────────────────────────────────────────

private fun DrawScope.drawDonutChart(segments: List<Pair<Float, Color>>) {
    val total     = segments.sumOf { it.first.toDouble() }.toFloat()
    val stroke    = size.minDimension * 0.22f
    val radius    = (size.minDimension - stroke) / 2f
    val center    = Offset(size.width / 2f, size.height / 2f)

    var startAngle = -90f
    segments.forEach { (value, color) ->
        val sweep = (value / total) * 360f
        drawArc(
            color       = color,
            startAngle  = startAngle,
            sweepAngle  = sweep - 2f,
            useCenter   = false,
            topLeft     = Offset(center.x - radius, center.y - radius),
            size        = Size(radius * 2f, radius * 2f),
            style       = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
        startAngle += sweep
    }
}

@Composable
private fun MetricCard(label: String, value: String, color: Color, modifier: Modifier) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, color = color, fontWeight = FontWeight.SemiBold)
        }
    }
}
