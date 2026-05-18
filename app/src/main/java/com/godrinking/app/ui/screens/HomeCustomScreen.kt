package com.godrinking.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godrinking.app.ui.components.ScreenTopBar
import com.godrinking.app.ui.theme.GoDrinkingTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HomeCustomScreen(
    activeWidgets: List<String>,
    onToggleWidget: (String) -> Unit,
    onBack: () -> Unit
) {
    val allWidgets = listOf(
        "metrics"           to "Resumo de Métricas",
        "calendar"          to "Calendário de Eventos",
        "upcoming_events"   to "Próximos Eventos",
        "stock_alerts"      to "Alertas de Estoque",
        "financial_summary" to "Resumo Financeiro",
        "quick_actions"     to "Ações Rápidas"
    )


    Scaffold(
        topBar = { ScreenTopBar(title = "Personalizar Início", onBack = onBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Escolha quais blocos aparecem na sua tela inicial:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(allWidgets) { (id, label) ->
                val isEnabled = activeWidgets.contains(id)
                Card(
                    onClick = { onToggleWidget(id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isEnabled) 
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(label, style = MaterialTheme.typography.titleSmall)
                        }
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { onToggleWidget(id) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeCustomScreenPreview() {
    GoDrinkingTheme {
        HomeCustomScreen(
            activeWidgets = listOf("metrics", "calendar"),
            onToggleWidget = {},
            onBack = {}
        )
    }
}
