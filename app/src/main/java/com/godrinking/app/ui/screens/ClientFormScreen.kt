package com.godrinking.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godrinking.app.ui.components.LabeledTextField
import com.godrinking.app.ui.components.ScreenTopBar
import com.godrinking.app.ui.theme.PrimaryRed

@Composable
fun ClientFormScreen(onBack: () -> Unit) {
    var name     by remember { mutableStateOf("") }
    var document by remember { mutableStateOf("") }
    var phone    by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var cep      by remember { mutableStateOf("") }
    var street   by remember { mutableStateOf("") }
    var number   by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var city     by remember { mutableStateOf("") }
    var state    by remember { mutableStateOf("SP") }
    var status   by remember { mutableStateOf("Em andamento") }
    val statusOptions = listOf("Em andamento", "Finalizado - Convertido", "Finalizado - Perdido")

    Scaffold(
        topBar = { ScreenTopBar(title = "Novo Cliente", onBack = onBack) }
    ) { padding ->
        LazyColumn(
            modifier        = Modifier.fillMaxSize().padding(padding),
            contentPadding  = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Dados pessoais ─────────────────────────────────────────────
            item {
                FormSection(title = "Dados Pessoais") {
                    LabeledTextField("Nome Completo / Razão Social *", name, { name = it }, "Ex: Maria Silva")
                    Spacer(Modifier.height(12.dp))
                    LabeledTextField("CPF / CNPJ *", document, { document = it }, "000.000.000-00")
                    Spacer(Modifier.height(12.dp))
                    LabeledTextField("Telefone *", phone, { phone = it }, "(11) 98765-4321")
                    Spacer(Modifier.height(12.dp))
                    LabeledTextField("E-mail *", email, { email = it }, "email@exemplo.com")
                }
            }

            // ── Endereço ───────────────────────────────────────────────────
            item {
                FormSection(title = "Endereço") {
                    LabeledTextField("CEP", cep, { cep = it }, "00000-000")
                    Spacer(Modifier.height(12.dp))
                    LabeledTextField("Logradouro", street, { street = it }, "Rua, Avenida...")
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            LabeledTextField("Número", number, { number = it }, "123")
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            LabeledTextField("Bairro", district, { district = it }, "Centro")
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(2f)) {
                            LabeledTextField("Cidade", city, { city = it }, "São Paulo")
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            LabeledTextField("Estado", state, { state = it }, "SP")
                        }
                    }
                }
            }

            // ── Status no funil ────────────────────────────────────────────
            item {
                FormSection(title = "Status no Funil") {
                    Text("Status *", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        statusOptions.forEach { option ->
                            Row(
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = status == option,
                                    onClick  = { status = option },
                                    colors   = RadioButtonDefaults.colors(selectedColor = PrimaryRed)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(option, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            // ── Botões ─────────────────────────────────────────────────────
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 4.dp)) {
                    OutlinedButton(
                        onClick  = onBack,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape    = RoundedCornerShape(12.dp)
                    ) { Text("Cancelar") }
                    Button(
                        onClick  = { /* salvar cliente */ onBack() },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                        enabled  = name.isNotBlank() && phone.isNotBlank()
                    ) {
                        Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Salvar", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.padding(bottom = 12.dp))
            content()
        }
    }
}
