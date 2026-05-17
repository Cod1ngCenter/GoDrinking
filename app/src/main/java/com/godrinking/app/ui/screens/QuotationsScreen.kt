package com.godrinking.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godrinking.app.data.PackageType
import com.godrinking.app.data.Quotation
import com.godrinking.app.data.SampleData
import com.godrinking.app.ui.components.ScreenTopBar
import com.godrinking.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun QuotationsScreen(onBack: () -> Unit, onNewQuotation: () -> Unit) {
    var quotations by remember { mutableStateOf(SampleData.quotations) }
    val today = remember { Calendar.getInstance() }
    val year = today.get(Calendar.YEAR)

    Scaffold(
        topBar = {
            ScreenTopBar(title = "Orçamentos", onBack = onBack)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewQuotation,
                containerColor = PrimaryRed,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, "Novo Orçamento")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier        = Modifier.fillMaxSize().padding(padding),
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
                                Icon(Icons.Default.Calculate, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Total em $year", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(quotations.size.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(0.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(GreenSuccess), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.TrendingUp, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Aprovados", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            val approved = quotations.count { it.status == "Aprovado" }
                            Text(approved.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            items(quotations) { quotation ->
                QuotationCard(quotation = quotation)
            }
        }
    }
}

@Composable
private fun QuotationCard(quotation: Quotation) {
    val (statusColor, statusBg) = when (quotation.status) {
        "Aprovado" -> GreenSuccess to GreenSuccessBg
        "Pendente" -> AmberAccent to AmberAccentBg
        else       -> RedDanger to RedDangerBg
    }
    val (pkgColor, pkgBg) = when (quotation.packageType) {
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
                    Text(quotation.name, fontWeight = FontWeight.Medium, fontSize = 14.sp, maxLines = 1)
                    Text(quotation.client, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(statusBg).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(quotation.status, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(pkgBg).padding(horizontal = 8.dp, vertical = 3.dp)) {
                        Text(quotation.packageType.label.replace("Pacote ", ""), color = pkgColor, fontSize = 10.sp)
                    }
                    Text("${quotation.people} pess.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("R$ ${"%.0f".format(quotation.value)}", color = PrimaryRed, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text("Gerado em ${dateFmt.format(quotation.date)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
