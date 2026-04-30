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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godrinking.app.data.UserRole
import com.godrinking.app.ui.components.ScreenTopBar
import com.godrinking.app.ui.theme.*

private val avatarColors = listOf(
    PrimaryRed, PrimaryRedDark, PrimaryRedDarker,
    Color(0xFFEA580C), Color(0xFFD97706), Color(0xFF374151)
)

@Composable
fun ProfileScreen(
    userName: String,
    userRole: UserRole,
    onBack: () -> Unit
) {
    var isEditing   by remember { mutableStateOf(false) }
    var avatarColor by remember { mutableStateOf(PrimaryRed) }
    var name        by remember { mutableStateOf(userName) }
    var email       by remember { mutableStateOf(
        if (userRole == UserRole.GERENTE) "carlos@godrinking.com.br" else "ana@godrinking.com.br"
    )}
    var phone       by remember { mutableStateOf("(11) 99999-1234") }
    var role        by remember { mutableStateOf(
        if (userRole == UserRole.GERENTE) "Gerente de Operações" else "Desenvolvedor(a)"
    )}
    var bio         by remember { mutableStateOf("Equipe Go Drinking! – Open bar e eventos personalizados.") }

    val initials = name.split(" ").take(2).joinToString("") { it.take(1).uppercase() }

    Scaffold(
        topBar = {
            ScreenTopBar(title = "Meu Perfil", onBack = onBack, actions = {
                if (isEditing) {
                    Button(
                        onClick  = { isEditing = false },
                        modifier = Modifier.padding(end = 8.dp),
                        shape    = RoundedCornerShape(8.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Save, null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Salvar", fontSize = 13.sp)
                    }
                } else {
                    OutlinedButton(
                        onClick  = { isEditing = true },
                        modifier = Modifier.padding(end = 8.dp),
                        shape    = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Editar", fontSize = 13.sp)
                    }
                }
            })
        }
    ) { padding ->
        LazyColumn(
            modifier        = Modifier.fillMaxSize().padding(padding),
            contentPadding  = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Avatar ─────────────────────────────────────────────────────
            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box {
                            Box(
                                modifier         = Modifier.size(88.dp).clip(CircleShape).background(avatarColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(initials, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                            }
                            if (isEditing) {
                                Box(
                                    modifier         = Modifier
                                        .size(28.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surface)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        if (isEditing) {
                            Spacer(Modifier.height(14.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                avatarColors.forEach { color ->
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .border(if (avatarColor == color) 2.dp else 0.dp, Color.White, CircleShape)
                                            .clickable { avatarColor = color }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Text(name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                            Icon(Icons.Default.Shield, null, modifier = Modifier.size(14.dp), tint = PrimaryRed)
                            Spacer(Modifier.width(4.dp))
                            Text(userRole.name, fontSize = 13.sp, color = PrimaryRed)
                        }
                    }
                }
            }

            // ── Informações pessoais ───────────────────────────────────────
            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column {
                        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                            Text("Informações Pessoais", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
                        ProfileInfoRow(Icons.Default.Person,  "Nome completo", name, isEditing) { name = it }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
                        ProfileInfoRow(Icons.Default.Email,   "E-mail",        email, isEditing) { email = it }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
                        ProfileInfoRow(Icons.Default.Phone,   "Telefone",      phone, isEditing) { phone = it }
                    }
                }
            }

            // ── Cargo e bio ────────────────────────────────────────────────
            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column {
                        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                            Text("Cargo & Função", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
                        ProfileInfoRow(Icons.Default.Work, "Cargo", role, isEditing) { role = it }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
                        Row(modifier = Modifier.padding(16.dp)) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.size(20.dp).padding(top = 2.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Sobre", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(4.dp))
                                if (isEditing) {
                                    OutlinedTextField(
                                        value         = bio,
                                        onValueChange = { bio = it },
                                        modifier      = Modifier.fillMaxWidth(),
                                        shape         = RoundedCornerShape(8.dp),
                                        minLines      = 2,
                                        colors        = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed)
                                    )
                                } else {
                                    Text(bio, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            // ── Nível de acesso ────────────────────────────────────────────
            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nível de Acesso", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 10.dp))
                        val isGerente = userRole == UserRole.GERENTE
                        Row(
                            modifier          = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isGerente) PrimaryRed.copy(0.08f) else BlueInfo.copy(0.08f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                null,
                                modifier = Modifier.size(26.dp),
                                tint     = if (isGerente) PrimaryRed else BlueInfo
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(userRole.name, fontSize = 14.sp, color = if (isGerente) PrimaryRed else BlueInfo, fontWeight = FontWeight.SemiBold)
                                Text(
                                    if (isGerente) "Acesso a gestão de clientes, eventos e relatórios"
                                    else           "Acesso total ao sistema, incluindo configurações avançadas",
                                    fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    editing: Boolean,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier          = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(2.dp))
            if (editing) {
                OutlinedTextField(
                    value         = value,
                    onValueChange = onValueChange,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(8.dp),
                    singleLine    = true,
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor    = PrimaryRed,
                        focusedContainerColor    = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor  = MaterialTheme.colorScheme.surfaceVariant,
                    )
                )
            } else {
                Text(value, fontSize = 14.sp)
            }
        }
    }
}
