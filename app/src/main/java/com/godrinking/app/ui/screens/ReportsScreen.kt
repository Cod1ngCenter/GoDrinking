package com.godrinking.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.atan2
import kotlin.math.PI
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
                        var animationPlayed by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { animationPlayed = true }

                        weeklyData.forEachIndexed { i, value ->
                            val animatedHeightProgress by animateFloatAsState(
                                targetValue = if (animationPlayed) value / maxVal else 0f,
                                animationSpec = tween(durationMillis = 1000, delayMillis = i * 100),
                                label = "BarHeightAnimation"
                            )

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
                                        .fillMaxHeight(animatedHeightProgress)
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
            var selectedSegment by remember { mutableStateOf<PieSegment?>(null) }

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
                            var animationPlayed by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) { animationPlayed = true }
                            val sweepProgress by animateFloatAsState(
                                targetValue = if (animationPlayed) 1f else 0f,
                                animationSpec = tween(durationMillis = 1200),
                                label = "DonutChartAnimation"
                            )

                            val segments = pieData
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(Unit) {
                                        detectTapGestures { offset ->
                                            val center = Offset(size.width / 2f, size.height / 2f)
                                            val x = offset.x - center.x
                                            val y = offset.y - center.y
                                            var angle = atan2(y, x) * (180f / PI.toFloat())
                                            if (angle < 0) angle += 360f

                                            // Corrigir para o ponto de partida do gráfico (-90 graus)
                                            val adjustedAngle = (angle + 90f) % 360f
                                            
                                            var currentAngle = 0f
                                            val totalVal = pieData.sumOf { it.value }
                                            
                                            val clicked = pieData.find { seg ->
                                                val sweep = (seg.value.toFloat() / totalVal) * 360f
                                                val start = currentAngle
                                                val end = currentAngle + sweep
                                                currentAngle += sweep
                                                adjustedAngle in start..end
                                            }
                                            
                                            selectedSegment = if (selectedSegment == clicked) null else clicked
                                        }
                                    }
                            ) {
                                drawDonutChart(
                                    segments = segments.map { Pair(it.value.toFloat(), it.color) },
                                    progress = sweepProgress,
                                    selectedIndex = segments.indexOf(selectedSegment)
                                )
                            }
                            
                            // Texto central animado
                            AnimatedContent(
                                targetState = selectedSegment,
                                transitionSpec = {
                                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                                },
                                label = "CenterTextAnimation"
                            ) { targetSegment ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    if (targetSegment == null) {
                                        Text("Total", fontSize = 10.sp, color = Color.Gray)
                                        Text("R$${total / 1000}k", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Text(targetSegment.name, fontSize = 10.sp, color = targetSegment.color, fontWeight = FontWeight.Medium)
                                        Text("R$${targetSegment.value / 1000}k", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.width(16.dp))

                        // Legenda
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            pieData.forEach { seg ->
                                val isSelected = selectedSegment == seg
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) seg.color.copy(alpha = 0.1f) else Color.Transparent)
                                        .clickable { 
                                            selectedSegment = if (isSelected) null else seg
                                        }
                                        .padding(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(if (isSelected) 12.dp else 10.dp)
                                            .clip(CircleShape)
                                            .background(seg.color)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            seg.name, 
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            "R$ ${seg.value / 1000}k · ${(seg.value * 100 / total)}%",
                                            fontSize = 10.sp,
                                            color    = if (isSelected) seg.color else MaterialTheme.colorScheme.onSurfaceVariant
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

private fun DrawScope.drawDonutChart(
    segments: List<Pair<Float, Color>>, 
    progress: Float,
    selectedIndex: Int = -1
) {
    val total     = segments.sumOf { it.first.toDouble() }.toFloat()
    val stroke    = size.minDimension * 0.22f
    val radius    = (size.minDimension - stroke) / 2f
    val center    = Offset(size.width / 2f, size.height / 2f)

    var startAngle = -90f
    segments.forEachIndexed { index, (value, color) ->
        val sweep = (value / total) * 360f * progress
        val isSelected = index == selectedIndex
        
        if (sweep > 0.1f) {
            drawArc(
                color       = if (selectedIndex == -1 || isSelected) color else color.copy(alpha = 0.2f),
                startAngle  = startAngle,
                sweepAngle  = sweep - 1f,
                useCenter   = false,
                topLeft     = Offset(center.x - radius, center.y - radius),
                size        = Size(radius * 2f, radius * 2f),
                style       = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = if (isSelected) stroke * 1.2f else stroke
                )
            )
        }
        startAngle += (value / total) * 360f
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
