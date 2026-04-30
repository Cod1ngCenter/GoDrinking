package com.godrinking.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godrinking.app.ui.theme.*

// ── Topo de tela padrão (overlays / telas secundárias) ───────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTopBar(
    title: String,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title              = { Text(title, fontWeight = FontWeight.SemiBold) },
        navigationIcon     = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, contentDescription = "Fechar")
            }
        },
        actions            = actions,
        colors             = TopAppBarDefaults.topAppBarColors(
            containerColor     = MaterialTheme.colorScheme.surface,
            titleContentColor  = MaterialTheme.colorScheme.onSurface,
        )
    )
}

// ── Badge colorido de status ──────────────────────────────────────────────────

@Composable
fun StatusBadge(label: String, textColor: Color, bgColor: Color) {
    Box(
        modifier         = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(label, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

// ── Card padrão do app ────────────────────────────────────────────────────────

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border   = ButtonDefaults.outlinedButtonBorder(true)
            .copy(width = 1.dp)
            .let { null }, // usamos border manual
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            content = content
        )
    }
}

// ── Botão primário padrão ─────────────────────────────────────────────────────

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick  = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        enabled  = enabled,
        shape    = RoundedCornerShape(12.dp),
        colors   = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

// ── Botão secundário padrão ───────────────────────────────────────────────────

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick  = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape    = RoundedCornerShape(12.dp),
        colors   = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text, fontWeight = FontWeight.Medium, fontSize = 15.sp)
    }
}

// ── Campo de texto com label ──────────────────────────────────────────────────

@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text     = label,
            fontSize = 12.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value            = value,
            onValueChange    = onValueChange,
            placeholder      = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            modifier         = Modifier.fillMaxWidth(),
            shape            = RoundedCornerShape(10.dp),
            singleLine       = true,
            colors           = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = PrimaryRed,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        )
    }
}

// ── Contador +/- ─────────────────────────────────────────────────────────────

@Composable
fun NumberCounter(
    value: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier          = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilledTonalIconButton(
            onClick = onDecrement,
            colors  = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) { Text("−", fontSize = 18.sp, fontWeight = FontWeight.Bold) }

        Text(
            text  = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        FilledIconButton(
            onClick = onIncrement,
            colors  = IconButtonDefaults.filledIconButtonColors(containerColor = PrimaryRed)
        ) { Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White) }
    }
}

// ── Seletor de categoria em chips ─────────────────────────────────────────────

@Composable
fun CategoryChipRow(
    categories: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        categories.forEach { cat ->
            val isSelected = cat == selected
            FilterChip(
                selected = isSelected,
                onClick  = { onSelect(cat) },
                label    = { Text(cat, fontSize = 13.sp) },
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor      = PrimaryRed,
                    selectedLabelColor          = Color.White,
                    selectedLeadingIconColor    = Color.White,
                )
            )
        }
    }
}
