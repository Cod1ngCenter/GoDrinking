package com.godrinking.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.godrinking.app.data.Event
import com.godrinking.app.data.PackageType
import com.godrinking.app.data.SampleData
import com.godrinking.app.ui.components.ScreenTopBar
import com.godrinking.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

private val MONTH_SHORT = arrayOf("Jan","Fev","Mar","Abr","Mai","Jun","Jul","Ago","Set","Out","Nov","Dez")
private val MONTH_FULL  = arrayOf("Janeiro","Fevereiro","Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro")
private val DAY_LABELS  = arrayOf("D","S","T","Q","Q","S","S")

@Composable
fun EventsScreen(onBack: () -> Unit, onNewEvent: () -> Unit = {}) {
    val today        = remember { Calendar.getInstance() }
    var events       by remember { mutableStateOf(SampleData.events) }
    var selectedMonth by remember { mutableIntStateOf(today.get(Calendar.MONTH)) }
    var yearOffset   by remember { mutableIntStateOf(0) }
    var detailEvent  by remember { mutableStateOf<Event?>(null) }
    val year         = today.get(Calendar.YEAR) + yearOffset

    // Agrupar dias de eventos por mês
    val eventsByMonth = remember(events, year) {
        val map = mutableMapOf<Int, MutableList<Int>>()
        events.forEach { e ->
            val cal = Calendar.getInstance().apply { time = e.date }
            if (cal.get(Calendar.YEAR) == year) {
                val m = cal.get(Calendar.MONTH)
                map.getOrPut(m) { mutableListOf() }.add(cal.get(Calendar.DAY_OF_MONTH))
            }
        }
        map
    }

    val filteredEvents = events.filter { e ->
        val cal = Calendar.getInstance().apply { time = e.date }
        cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == selectedMonth
    }.sortedBy { it.date }

    val totalEvents  = events.count  { Calendar.getInstance().apply { time = it.date }.get(Calendar.YEAR) == year }
    val totalRevenue = events.filter { Calendar.getInstance().apply { time = it.date }.get(Calendar.YEAR) == year }.sumOf { it.finalValue }

    if (detailEvent != null) {
        EventDetailDialog(event = detailEvent!!, onClose = { detailEvent = null })
    }

    LazyColumn(
        modifier        = Modifier.fillMaxSize(),
        contentPadding  = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
            // ── Stats ──────────────────────────────────────────────────────
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(0.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(PrimaryRed), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CalendarMonth, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Eventos em $year", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(totalEvents.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold)
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
                            Text("Receita em $year", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("R$ ${"%.1f".format(totalRevenue / 1000)}k", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ── Calendário anual ───────────────────────────────────────────
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { yearOffset-- }) {
                                Icon(Icons.Default.ChevronLeft, null)
                            }
                            Text(year.toString(), fontWeight = FontWeight.Medium)
                            IconButton(onClick = { yearOffset++ }) {
                                Icon(Icons.Default.ChevronRight, null)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        // 4 linhas × 3 colunas
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            (0..3).forEach { row ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    (0..2).forEach { col ->
                                        val m = row * 3 + col
                                        MiniMonthCard(
                                            year       = year,
                                            month      = m,
                                            today      = today,
                                            eventDays  = eventsByMonth[m] ?: emptyList(),
                                            selected   = selectedMonth == m,
                                            onClick    = { selectedMonth = m },
                                            modifier   = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Eventos do mês selecionado ─────────────────────────────────
            item {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("${MONTH_FULL[selectedMonth]} $year", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Text("${filteredEvents.size} evento${if (filteredEvents.size != 1) "s" else ""}",
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (filteredEvents.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(0.dp)) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f))
                            Spacer(Modifier.height(8.dp))
                            Text("Nenhum evento neste mês", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = onNewEvent, colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed), shape = RoundedCornerShape(8.dp)) {
                                Text("Cadastrar evento", fontSize = 13.sp)
                            }
                        }
                    }
                }
            } else {
                items(filteredEvents) { event ->
                    EventCard(event = event, onDetail = { detailEvent = event })
                }
            }
        }
}

@Composable
private fun MiniMonthCard(
    year: Int, month: Int, today: Calendar,
    eventDays: List<Int>, selected: Boolean,
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val cal = Calendar.getInstance().apply { set(year, month, 1) }
    val daysInMonth  = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
    val isCurrentMonth = today.get(Calendar.YEAR) == year && today.get(Calendar.MONTH) == month

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.5.dp,
                color = if (selected) PrimaryRed else MaterialTheme.colorScheme.outline.copy(0.4f),
                shape = RoundedCornerShape(8.dp)
            )
            .background(if (selected) PrimaryRed.copy(0.08f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(6.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(MONTH_SHORT[month], fontSize = 9.sp, color = if (selected) PrimaryRed else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                if (isCurrentMonth) {
                    Spacer(Modifier.width(2.dp))
                    Box(modifier = Modifier.size(4.dp).clip(androidx.compose.foundation.shape.CircleShape).background(PrimaryRed))
                }
            }
            Spacer(Modifier.height(2.dp))
            // Mini grid
            val cells = firstDayOfWeek + daysInMonth
            val rows  = (cells + 6) / 7
            var d     = 1 - firstDayOfWeek
            repeat(rows) {
                Row {
                    repeat(7) {
                        val day = d++
                        Box(
                            modifier         = Modifier.weight(1f).aspectRatio(1f)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    when {
                                        day in 1..daysInMonth && eventDays.contains(day) -> PrimaryRed
                                        day == today.get(Calendar.DAY_OF_MONTH) && isCurrentMonth -> MaterialTheme.colorScheme.surfaceVariant
                                        else -> Color.Transparent
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (day in 1..daysInMonth) {
                                Text(day.toString(), fontSize = 5.sp, color = if (eventDays.contains(day)) Color.White else MaterialTheme.colorScheme.onSurface.copy(0.7f))
                            }
                        }
                    }
                }
            }
            if (eventDays.isNotEmpty()) {
                Text("${eventDays.size}ev", fontSize = 7.sp, color = if (selected) PrimaryRed else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}

@Composable
private fun EventCard(event: Event, onDetail: () -> Unit) {
    val (pkgColor, pkgBg) = when (event.packageType) {
        PackageType.ORLOFF   -> BlueInfo     to BlueInfoBg
        PackageType.SMIRNOFF -> AmberAccent  to AmberAccentBg
        PackageType.ABSOLUT  -> PurpleAccent to PurpleAccentBg
    }
    val dateFmt = SimpleDateFormat("dd MMM", Locale("pt","BR"))

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
                    Text(event.name, fontWeight = FontWeight.Medium, fontSize = 14.sp, maxLines = 1)
                    Text(event.client, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(PrimaryRed.copy(0.1f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(dateFmt.format(event.date), color = PrimaryRed, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(3.dp))
                    Text(event.location, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(3.dp))
                    Text("${event.people}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(pkgBg).padding(horizontal = 8.dp, vertical = 3.dp)) {
                        Text(event.packageType.label.replace("Pacote ", ""), color = pkgColor, fontSize = 10.sp)
                    }
                    if (event.services.isNotEmpty()) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 8.dp, vertical = 3.dp)) {
                            Text("+${event.services.size} serv.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("R$ ${"%.0f".format(event.finalValue)}", color = PrimaryRed, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(Modifier.width(6.dp))
                    IconButton(onClick = onDetail, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Visibility, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

// ── Dialog de detalhes do evento ─────────────────────────────────────────────

@Composable
private fun EventDetailDialog(event: Event, onClose: () -> Unit) {
    val (pkgColor, _) = when (event.packageType) {
        PackageType.ORLOFF   -> BlueInfo     to BlueInfoBg
        PackageType.SMIRNOFF -> AmberAccent  to AmberAccentBg
        PackageType.ABSOLUT  -> PurpleAccent to PurpleAccentBg
    }
    val dateFmt = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("pt","BR"))

    Dialog(onDismissRequest = onClose) {
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(20.dp),
            colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(event.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                    IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp))
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        InfoItem("Cliente", event.client, Modifier.weight(1f))
                        InfoItem("Tipo", event.type, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        InfoItem("Data", dateFmt.format(event.date), Modifier.weight(1f))
                        InfoItem("Horário", event.time, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        InfoItem("Pessoas", "${event.people}", Modifier.weight(1f))
                        Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(10.dp)) {
                            Column {
                                Text("Pacote", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(event.packageType.label.replace("Pacote ", ""), fontSize = 13.sp, color = pkgColor, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        InfoItem("Pessoas", "${event.people}", Modifier.weight(1f))
                        Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(10.dp)) {
                            Column {
                                Text("Pacote", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(event.packageType.label.replace("Pacote ", ""), fontSize = 13.sp, color = pkgColor, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                    InfoItem("Local", event.location)
                    if (event.services.isNotEmpty()) {
                        Column(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(10.dp)) {
                            Text("Serviços", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                event.services.forEach { s ->
                                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(MaterialTheme.colorScheme.surface).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                        Text(s, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                    if (event.observations.isNotBlank()) {
                        InfoItem("Observações", event.observations)
                    }
                    // Valor final
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(PrimaryRed.copy(0.08f)).padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Valor Final do Evento", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("R$ ${"%.2f".format(event.finalValue)}", color = PrimaryRed, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(10.dp)) {
        Column {
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(2.dp))
            Text(value, fontSize = 13.sp)
        }
    }
}
